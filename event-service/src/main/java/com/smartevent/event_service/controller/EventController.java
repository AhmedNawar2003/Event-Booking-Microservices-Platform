package com.smartevent.event_service.controller;

import com.smartevent.event_service.dto.request.CreateEventRequest;
import com.smartevent.event_service.dto.request.UpdateEventRequest;
import com.smartevent.event_service.dto.response.EventResponse;
import com.smartevent.event_service.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // Public - Get all published events with search & pagination
    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvents(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(eventService.getAllPublishedEvents(title, page, size));
    }

    // Public - Get event by id
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable String id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    // Protected - Get my events (organizer)
    @GetMapping("/my")
    public ResponseEntity<Page<EventResponse>> getMyEvents(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                eventService.getMyEvents(authentication.getName(), page, size)
        );
    }

    // Protected - Create event
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(request, authentication.getName()));
    }

    // Protected - Update event
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable String id,
            @Valid @RequestBody UpdateEventRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                eventService.updateEvent(id, request, authentication.getName())
        );
    }

    // Protected - Publish event
    @PatchMapping("/{id}/publish")
    public ResponseEntity<EventResponse> publishEvent(
            @PathVariable String id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                eventService.publishEvent(id, authentication.getName())
        );
    }

    // Protected - Cancel event
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<EventResponse> cancelEvent(
            @PathVariable String id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                eventService.cancelEvent(id, authentication.getName())
        );
    }

    // Protected - Delete event
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable String id,
            Authentication authentication
    ) {
        eventService.deleteEvent(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    // Internal - Update available seats (called by booking-service)
    @PutMapping("/{id}/seats")
    public ResponseEntity<EventResponse> updateAvailableSeats(
            @PathVariable String id,
            @RequestParam int seats
    ) {
        System.out.println(">>> updateAvailableSeats called: id=" + id + ", seats=" + seats);
        return ResponseEntity.ok(eventService.updateAvailableSeats(id, seats));
    }
}