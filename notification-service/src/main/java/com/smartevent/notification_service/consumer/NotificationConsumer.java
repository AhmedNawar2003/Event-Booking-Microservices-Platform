package com.smartevent.notification_service.consumer;

import com.smartevent.notification_service.config.KafkaConfig;
import com.smartevent.notification_service.dto.BookingEvent;
import com.smartevent.notification_service.dto.EventCancelledEvent;
import com.smartevent.notification_service.dto.PaymentEvent;
import com.smartevent.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = KafkaConfig.BOOKING_CREATED_TOPIC,
            groupId = "notification-group",
            containerFactory = "bookingKafkaListenerContainerFactory"
    )
    public void handleBookingCreated(BookingEvent event) {
        log.info("Received booking event: {}", event.getBookingId());
        notificationService.sendBookingConfirmation(event);
    }

    @KafkaListener(
            topics = KafkaConfig.PAYMENT_SUCCESS_TOPIC,
            groupId = "notification-group",
            containerFactory = "paymentKafkaListenerContainerFactory" // ← ضيف دي
    )
    public void handlePaymentSuccess(PaymentEvent event) {
        log.info("Received payment success event: {}", event.getPaymentId());
        notificationService.sendPaymentSuccess(event);
    }

    @KafkaListener(
            topics = KafkaConfig.PAYMENT_FAILED_TOPIC,
            groupId = "notification-group",
            containerFactory = "paymentKafkaListenerContainerFactory" // ← ضيف دي
    )
    public void handlePaymentFailed(PaymentEvent event) {
        log.info("Received payment failed event: {}", event.getPaymentId());
        notificationService.sendPaymentFailed(event);
    }

    @KafkaListener(
            topics = KafkaConfig.EVENT_CANCELLED_TOPIC,
            groupId = "notification-group",
            containerFactory = "eventCancelledKafkaListenerContainerFactory" // ← ضيف دي
    )
    public void handleEventCancelled(EventCancelledEvent event) {
        log.info("Received event cancelled: {}", event.getEventId());
        notificationService.sendEventCancelled(event);
    }
}