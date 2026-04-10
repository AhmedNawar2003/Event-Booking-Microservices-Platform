package com.smartevent.payment_service.controller;


import com.smartevent.payment_service.dto.request.ProcessPaymentRequest;
import com.smartevent.payment_service.dto.response.PaymentResponse;
import com.smartevent.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Process payment
    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody ProcessPaymentRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.processPayment(request, authentication.getName()));
    }

    // Get payment by id
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable String id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    // Get payment by booking
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse> getPaymentByBooking(
            @PathVariable String bookingId
    ) {
        return ResponseEntity.ok(paymentService.getPaymentByBooking(bookingId));
    }

    // Get my payments
    @GetMapping("/my")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                paymentService.getMyPayments(authentication.getName())
        );
    }

    // Refund payment
    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable String id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                paymentService.refundPayment(id, authentication.getName())
        );
    }
}