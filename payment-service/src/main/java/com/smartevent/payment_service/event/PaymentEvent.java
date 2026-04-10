package com.smartevent.payment_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private String paymentId;
    private String bookingId;
    private String userId;
    private String userEmail;
    private Double amount;
    private String transactionId;
    private String failureReason;
}
