package com.smartevent.event_service.service;

import com.smartevent.event_service.dto.request.CreateEventRequest;
import com.smartevent.event_service.dto.request.UpdateEventRequest;
import com.smartevent.event_service.dto.response.EventResponse;
import com.smartevent.event_service.entity.Event;
import com.smartevent.event_service.entity.EventStatus;
import com.smartevent.event_service.exception.EventNotFoundException;
import com.smartevent.event_service.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;

    // ============ Create Event ============
    @Transactional
    public EventResponse createEvent(CreateEventRequest request, String organizerId) {

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalSeats(request.getTotalSeats())
                .availableSeats(request.getTotalSeats())
                .price(request.getPrice())
                .status(EventStatus.DRAFT)
                .organizerId(organizerId)
                .build();

        EventResponse response = EventResponse.fromEvent(eventRepository.save(event));
        log.info("Event created: {} by organizer: {}", response.getId(), organizerId);
        return response;
    }

    // ============ Get All Published Events ============
    @Transactional(readOnly = true)
    public Page<EventResponse> getAllPublishedEvents(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").ascending());

        if (title != null && !title.isEmpty()) {
            return eventRepository
                    .findByTitleContainingIgnoreCaseAndStatus(title, EventStatus.PUBLISHED, pageable)
                    .map(EventResponse::fromEvent);
        }

        return eventRepository
                .findByStatus(EventStatus.PUBLISHED, pageable)
                .map(EventResponse::fromEvent);
    }

    // ============ Get Event By Id ============
    @Transactional(readOnly = true)
    public EventResponse getEventById(String id) {
        return EventResponse.fromEvent(
                eventRepository.findById(id)
                        .orElseThrow(() -> new EventNotFoundException(
                                "Event not found with id: " + id
                        ))
        );
    }

    // ============ Get My Events ============
    @Transactional(readOnly = true)
    public Page<EventResponse> getMyEvents(String organizerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return eventRepository
                .findByOrganizerId(organizerId, pageable)
                .map(EventResponse::fromEvent);
    }

    // ============ Update Event ============
    @Transactional
    public EventResponse updateEvent(String id, UpdateEventRequest request, String organizerId) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(
                        "Event not found with id: " + id
                ));

        if (!event.getOrganizerId().equals(organizerId)) {
            throw new RuntimeException("You can only update your own events");
        }

        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new RuntimeException("Cannot update a cancelled event");
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setPrice(request.getPrice());

        if (request.getTotalSeats() > event.getTotalSeats()) {
            int diff = request.getTotalSeats() - event.getTotalSeats();
            event.setAvailableSeats(event.getAvailableSeats() + diff);
        }
        event.setTotalSeats(request.getTotalSeats());

        EventResponse response = EventResponse.fromEvent(eventRepository.save(event));
        log.info("Event updated: {}", id);
        return response;
    }

    // ============ Publish Event ============
    @Transactional
    public EventResponse publishEvent(String id, String organizerId) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(
                        "Event not found with id: " + id
                ));

        if (!event.getOrganizerId().equals(organizerId)) {
            throw new RuntimeException("You can only publish your own events");
        }

        if (event.getStatus() != EventStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT events can be published");
        }

        event.setStatus(EventStatus.PUBLISHED);
        EventResponse response = EventResponse.fromEvent(eventRepository.save(event));
        log.info("Event published: {}", id);
        return response;
    }

    // ============ Cancel Event ============
    @Transactional
    public EventResponse cancelEvent(String id, String organizerId) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(
                        "Event not found with id: " + id
                ));

        if (!event.getOrganizerId().equals(organizerId)) {
            throw new RuntimeException("You can only cancel your own events");
        }

        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new RuntimeException("Event is already cancelled");
        }

        event.setStatus(EventStatus.CANCELLED);
        EventResponse response = EventResponse.fromEvent(eventRepository.save(event));
        log.info("Event cancelled: {}", id);
        return response;
    }

    // ============ Delete Event ============
    @Transactional
    public void deleteEvent(String id, String organizerId) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(
                        "Event not found with id: " + id
                ));

        if (!event.getOrganizerId().equals(organizerId)) {
            throw new RuntimeException("You can only delete your own events");
        }

        if (event.getStatus() == EventStatus.PUBLISHED) {
            throw new RuntimeException("Cannot delete a published event, cancel it first");
        }

        eventRepository.delete(event);
        log.info("Event deleted: {}", id);
    }

    // ============ Update Available Seats (Internal) ============
    @Transactional
    public EventResponse updateAvailableSeats(String id, int seats) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(
                        "Event not found with id: " + id
                ));

        if (event.getAvailableSeats() + seats < 0) {
            throw new RuntimeException("Not enough available seats");
        }

        event.setAvailableSeats(event.getAvailableSeats() + seats);
        EventResponse response = EventResponse.fromEvent(eventRepository.save(event));
        log.info("Available seats updated for event: {} | seats change: {}", id, seats);
        return response;
    }
}
