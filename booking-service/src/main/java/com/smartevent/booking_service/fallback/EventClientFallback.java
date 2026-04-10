package com.smartevent.booking_service.fallback;

import com.smartevent.booking_service.client.EventClient;
import com.smartevent.booking_service.dto.response.EventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventClientFallback implements EventClient {

    @Override
    public EventResponse getEventById(String id) {
        log.error("Event Service is down! Cannot get event: {}", id);
        throw new RuntimeException("Event Service is unavailable. Please try again later.");
    }

    @Override
    public EventResponse updateAvailableSeats(String id, int seats) {
        log.error("Event Service is down! Cannot update seats for event: {}", id);
        throw new RuntimeException("Event Service is unavailable. Cannot update seats.");
    }
}