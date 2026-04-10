package com.smartevent.booking_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private String id;
    private String title;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer availableSeats;
    private Double price;
    private String status;
    private String organizerId;
}