package com.smartevent.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCancelledEvent {
    private String eventId;
    private String eventTitle;
    private String organizerId;
    private List<String> attendeeEmails;
}