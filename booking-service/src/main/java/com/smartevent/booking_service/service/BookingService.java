package com.smartevent.booking_service.service;


import com.smartevent.booking_service.client.EventClient;
import com.smartevent.booking_service.dto.request.CreateBookingRequest;
import com.smartevent.booking_service.dto.response.BookingResponse;
import com.smartevent.booking_service.dto.response.EventResponse;
import com.smartevent.booking_service.entity.Booking;
import com.smartevent.booking_service.entity.BookingStatus;
import com.smartevent.booking_service.event.BookingEvent;
import com.smartevent.booking_service.exception.BookingNotFoundException;
import com.smartevent.booking_service.repository.BookingRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventClient eventClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // ============ Create Booking ============
    @Transactional
    @CircuitBreaker(name = "eventService", fallbackMethod = "createBookingFallback")
    @Retry(name = "eventService")
    public BookingResponse createBooking(CreateBookingRequest request, String attendeeId) {

        // 1. Get event details from event-service
        EventResponse event = eventClient.getEventById(request.getEventId());

        // 2. Check event is published
        if (!"PUBLISHED".equals(event.getStatus())) {
            throw new RuntimeException("Event is not available for booking");
        }

        // 3. Check if already booked
        boolean alreadyBooked = bookingRepository.existsByEventIdAndAttendeeIdAndStatusNot(
                request.getEventId(),
                attendeeId,
                BookingStatus.CANCELLED
        );
        if (alreadyBooked) {
            throw new RuntimeException("You already have a booking for this event");
        }

        // 4. Check available seats
        if (event.getAvailableSeats() < request.getNumberOfSeats()) {
            throw new RuntimeException("Not enough available seats. Available: "
                    + event.getAvailableSeats());
        }

        // 5. Calculate total price
        double totalPrice = event.getPrice() * request.getNumberOfSeats();

        // 6. Create booking
        Booking booking = Booking.builder()
                .eventId(request.getEventId())
                .attendeeId(attendeeId)
                .numberOfSeats(request.getNumberOfSeats())
                .totalPrice(totalPrice)
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        // 7. Update available seats in event-service
        try {
            eventClient.updateAvailableSeats(
                    request.getEventId(),
                    -request.getNumberOfSeats()
            );
        } catch (Exception e) {
            bookingRepository.delete(savedBooking);
            throw new RuntimeException("Failed to update seats, booking cancelled");
        }

        // 8. Send Kafka event
        BookingEvent bookingEvent = BookingEvent.builder()
                .bookingId(savedBooking.getId())
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .attendeeId(attendeeId)
                .attendeeEmail(attendeeId)
                .numberOfSeats(request.getNumberOfSeats())
                .totalPrice(totalPrice)
                .eventDate(event.getStartDate())
                .build();

        kafkaTemplate.send("booking-created", bookingEvent);
        log.info("Booking created successfully: {}", savedBooking.getId());

        return BookingResponse.fromBooking(savedBooking);
    }

    // Fallback - بيتفعل بس لو الـ event-service down فعلاً
    public BookingResponse createBookingFallback(
            CreateBookingRequest request,
            String attendeeId,
            Exception ex
    ) {
        // لو business exception → رميها زي ما هي للـ user
        if (ex instanceof RuntimeException
                && ex.getMessage() != null
                && !ex.getMessage().toLowerCase().contains("connection")
                && !ex.getMessage().toLowerCase().contains("timeout")
                && !ex.getMessage().toLowerCase().contains("unavailable")
                && !ex.getMessage().toLowerCase().contains("circuit")) {
            throw (RuntimeException) ex;
        }

        // لو الـ service فعلاً down
        log.error("Circuit Breaker triggered - Event Service is DOWN: {}", ex.getMessage());
        throw new RuntimeException(
                "Event Service is temporarily unavailable. Please try again later."
        );
    }

    // ============ Get Booking By Id ============
    public BookingResponse getBookingById(String id, String attendeeId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking not found with id: " + id
                ));

        if (!booking.getAttendeeId().equals(attendeeId)) {
            throw new RuntimeException("You can only view your own bookings");
        }

        return BookingResponse.fromBooking(booking);
    }

    // ============ Get My Bookings ============
    public Page<BookingResponse> getMyBookings(String attendeeId, int page, int size) {
        Pageable pageable = PageRequest.of(
                page, size, Sort.by("createdAt").descending()
        );
        return bookingRepository
                .findByAttendeeId(attendeeId, pageable)
                .map(BookingResponse::fromBooking);
    }

    // ============ Get Event Bookings (Organizer) ============
    public Page<BookingResponse> getEventBookings(String eventId, int page, int size) {
        Pageable pageable = PageRequest.of(
                page, size, Sort.by("createdAt").descending()
        );
        return bookingRepository
                .findByEventId(eventId, pageable)
                .map(BookingResponse::fromBooking);
    }

    // ============ Cancel Booking ============
    @Transactional
    @CircuitBreaker(name = "eventService", fallbackMethod = "cancelBookingFallback")
    public BookingResponse cancelBooking(String id, String attendeeId) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking not found with id: " + id
                ));

        if (!booking.getAttendeeId().equals(attendeeId)) {
            throw new RuntimeException("You can only cancel your own bookings");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking savedBooking = bookingRepository.save(booking);

        try {
            eventClient.updateAvailableSeats(
                    booking.getEventId(),
                    booking.getNumberOfSeats()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to return seats to event");
        }

        return BookingResponse.fromBooking(savedBooking);
    }

    // Fallback
    public BookingResponse cancelBookingFallback(
            String id,
            String attendeeId,
            Exception ex
    ) {
        // لو business exception → رميها زي ما هي
        if (ex instanceof RuntimeException
                && ex.getMessage() != null
                && !ex.getMessage().toLowerCase().contains("connection")
                && !ex.getMessage().toLowerCase().contains("timeout")
                && !ex.getMessage().toLowerCase().contains("unavailable")
                && !ex.getMessage().toLowerCase().contains("circuit")) {
            throw (RuntimeException) ex;
        }

        log.error("Circuit Breaker triggered - Event Service is DOWN: {}", ex.getMessage());
        throw new RuntimeException(
                "Event Service is temporarily unavailable. Please try again later."
        );
    }
}