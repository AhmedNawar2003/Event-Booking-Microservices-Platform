package com.smartevent.payment_service.dto.response;

import com.smartevent.payment_service.entity.Payment;
import com.smartevent.payment_service.entity.PaymentStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String id;
    private String bookingId;
    private String userId;
    private Double amount;
    private PaymentStatus status;
    private String cardLastFour;
    private String transactionId;
    private String failureReason;
    private LocalDateTime createdAt;

    public static PaymentResponse fromPayment(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .cardLastFour(payment.getCardLastFour())
                .transactionId(payment.getTransactionId())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
