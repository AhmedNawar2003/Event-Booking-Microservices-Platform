package com.smartevent.booking_service.client;

import com.smartevent.booking_service.config.FeignConfig;
import com.smartevent.booking_service.dto.response.EventResponse;
import com.smartevent.booking_service.fallback.EventClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "event-service",
        configuration = FeignConfig.class,
        fallback = EventClientFallback.class
)
public interface EventClient {

    @GetMapping("/api/events/{id}")
    EventResponse getEventById(@PathVariable("id") String id);

    @PutMapping("/api/events/{id}/seats")
    EventResponse updateAvailableSeats(
            @PathVariable("id") String id,
            @RequestParam("seats") int seats
    );
}