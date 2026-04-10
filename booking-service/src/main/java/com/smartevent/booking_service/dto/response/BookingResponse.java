package com.smartevent.booking_service.dto.response;


import com.smartevent.booking_service.entity.Booking;
import com.smartevent.booking_service.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private String id;
    private String eventId;
    private String attendeeId;
    private Integer numberOfSeats;
    private Double totalPrice;
    private BookingStatus status;
    private LocalDateTime createdAt;

    public static BookingResponse fromBooking(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .eventId(booking.getEventId())
                .attendeeId(booking.getAttendeeId())
                .numberOfSeats(booking.getNumberOfSeats())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}