package com.smartevent.payment_service.service;


import com.smartevent.payment_service.dto.request.ProcessPaymentRequest;
import com.smartevent.payment_service.dto.response.PaymentResponse;
import com.smartevent.payment_service.entity.Payment;
import com.smartevent.payment_service.entity.PaymentStatus;
import com.smartevent.payment_service.event.PaymentEvent;
import com.smartevent.payment_service.exception.PaymentNotFoundException;
import com.smartevent.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    // ============ Process Payment ============
    @Transactional
    public PaymentResponse processPayment(ProcessPaymentRequest request, String userId) {

        // تأكد مفيش payment تاني لنفس الـ booking
        paymentRepository.findByBookingIdAndStatus(request.getBookingId(), PaymentStatus.SUCCESS)
                .ifPresent(p -> {
                    throw new RuntimeException("Payment already processed for this booking");
                });

        // محاكاة الـ Payment Processing
        boolean paymentSuccess = simulatePaymentProcessing(request.getCardNumber());

        String transactionId = UUID.randomUUID().toString();
        String cardLastFour = request.getCardNumber()
                .substring(request.getCardNumber().length() - 4);

        Payment payment = Payment.builder()
                .bookingId(request.getBookingId())
                .userId(userId)
                .amount(request.getAmount())
                .cardLastFour(cardLastFour)
                .transactionId(transactionId)
                .status(paymentSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .failureReason(paymentSuccess ? null : "Insufficient funds")
                .build();


        Payment savedPayment = paymentRepository.save(payment);

        if (!paymentSuccess) {
            throw new RuntimeException("Payment failed: Insufficient funds");
        }

        log.info("Payment processed successfully: {}", transactionId);
        PaymentEvent paymentEvent = PaymentEvent.builder()
                .paymentId(savedPayment.getId())
                .bookingId(savedPayment.getBookingId())
                .userId(userId)
                .userEmail(userId) // الـ userId هو الـ email
                .amount(savedPayment.getAmount())
                .transactionId(transactionId)
                .failureReason(savedPayment.getFailureReason())
                .build();

        if (paymentSuccess) {
            kafkaTemplate.send("payment-success", paymentEvent);
            log.info("Payment success event sent to Kafka");
        } else {
            kafkaTemplate.send("payment-failed", paymentEvent);
            log.info("Payment failed event sent to Kafka");
            throw new RuntimeException("Payment failed: Insufficient funds");
        }
        return PaymentResponse.fromPayment(savedPayment);
    }

    // ============ Get Payment By Id ============
    public PaymentResponse getPaymentById(String id) {
        return PaymentResponse.fromPayment(
                paymentRepository.findById(id)
                        .orElseThrow(() -> new PaymentNotFoundException(
                                "Payment not found with id: " + id
                        ))
        );
    }

    // ============ Get Payment By Booking ============
    public PaymentResponse getPaymentByBooking(String bookingId) {
        return PaymentResponse.fromPayment(
                paymentRepository.findByBookingId(bookingId)
                        .orElseThrow(() -> new PaymentNotFoundException(
                                "Payment not found for booking: " + bookingId
                        ))
        );
    }

    // ============ Get My Payments ============
    public List<PaymentResponse> getMyPayments(String userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(PaymentResponse::fromPayment)
                .collect(Collectors.toList());
    }

    // ============ Refund Payment ============
    @Transactional
    public PaymentResponse refundPayment(String id, String userId) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found with id: " + id
                ));

        if (!payment.getUserId().equals(userId)) {
            throw new RuntimeException("You can only refund your own payments");
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException("Only successful payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        log.info("Payment refunded: {}", payment.getTransactionId());
        return PaymentResponse.fromPayment(paymentRepository.save(payment));
    }

    // ============ Simulate Payment ============
    private boolean simulatePaymentProcessing(String cardNumber) {
        // بنعمل محاكاة:
        // لو الكارت بيبدأ بـ 0000 → فشل
        // باقي الكروت → نجاح
        return !cardNumber.startsWith("0000");
    }
}
