package com.smartevent.payment_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPaymentRequest {

    @NotBlank(message = "Booking ID is required")
    private String bookingId;

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Card number is required")
    @Size(min = 16, max = 16, message = "Card number must be 16 digits")
    private String cardNumber;

    @NotBlank(message = "Card holder name is required")
    private String cardHolderName;

    @NotBlank(message = "Expiry date is required")
    private String expiryDate;

    @NotBlank(message = "CVV is required")
    @Size(min = 3, max = 4, message = "CVV must be 3 or 4 digits")
    private String cvv;
}