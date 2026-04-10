package com.smartevent.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent {
    private String bookingId;
    private String eventId;
    private String eventTitle;
    private String attendeeId;
    private String attendeeEmail;
    private Integer numberOfSeats;
    private Double totalPrice;
    private LocalDateTime eventDate;
}
