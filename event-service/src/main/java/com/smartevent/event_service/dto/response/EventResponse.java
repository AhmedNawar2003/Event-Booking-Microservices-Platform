package com.smartevent.event_service.dto.response;


import com.smartevent.event_service.entity.Event;
import com.smartevent.event_service.entity.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private String id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer totalSeats;
    private Integer availableSeats;
    private Double price;
    private EventStatus status;
    private String organizerId;
    private LocalDateTime createdAt;

    public static EventResponse fromEvent(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .totalSeats(event.getTotalSeats())
                .availableSeats(event.getAvailableSeats())
                .price(event.getPrice())
                .status(event.getStatus())
                .organizerId(event.getOrganizerId())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
