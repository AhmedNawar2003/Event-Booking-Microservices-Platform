package com.smartevent.notification_service.service;

import com.smartevent.notification_service.dto.BookingEvent;
import com.smartevent.notification_service.dto.EventCancelledEvent;
import com.smartevent.notification_service.dto.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    // ============ Booking Notifications ============

    public void sendBookingConfirmation(BookingEvent event) {
        log.info("====================================");
        log.info("📧 BOOKING CONFIRMATION");
        log.info("To: {}", event.getAttendeeEmail());
        log.info("Subject: Booking Confirmed - {}", event.getEventTitle());
        log.info("Body:");
        log.info("  Dear {},", event.getAttendeeId());
        log.info("  Your booking is confirmed!");
        log.info("  Event: {}", event.getEventTitle());
        log.info("  Date: {}", event.getEventDate());
        log.info("  Seats: {}", event.getNumberOfSeats());
        log.info("  Total: ${}", event.getTotalPrice());
        log.info("  Booking ID: {}", event.getBookingId());
        log.info("====================================");
    }

    // ============ Payment Notifications ============

    public void sendPaymentSuccess(PaymentEvent event) {
        log.info("====================================");
        log.info("💳 PAYMENT SUCCESS");
        log.info("To: {}", event.getUserEmail());
        log.info("Subject: Payment Successful");
        log.info("Body:");
        log.info("  Amount: ${}", event.getAmount());
        log.info("  Transaction ID: {}", event.getTransactionId());
        log.info("  Booking ID: {}", event.getBookingId());
        log.info("====================================");
    }

    public void sendPaymentFailed(PaymentEvent event) {
        log.info("====================================");
        log.info("❌ PAYMENT FAILED");
        log.info("To: {}", event.getUserEmail());
        log.info("Subject: Payment Failed");
        log.info("Body:");
        log.info("  Amount: ${}", event.getAmount());
        log.info("  Reason: {}", event.getFailureReason());
        log.info("  Booking ID: {}", event.getBookingId());
        log.info("====================================");
    }

    // ============ Event Cancelled Notifications ============

    public void sendEventCancelled(EventCancelledEvent event) {
        log.info("====================================");
        log.info("🚫 EVENT CANCELLED");
        log.info("Event: {}", event.getEventTitle());
        log.info("Notifying {} attendees", event.getAttendeeEmails().size());
        event.getAttendeeEmails().forEach(email -> {
            log.info("  → Sending cancellation to: {}", email);
        });
        log.info("====================================");
    }
}
