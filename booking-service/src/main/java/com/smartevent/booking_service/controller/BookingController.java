package com.smartevent.booking_service.controller;


import com.smartevent.booking_service.dto.request.CreateBookingRequest;
import com.smartevent.booking_service.dto.response.BookingResponse;
import com.smartevent.booking_service.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // Create booking
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(request, authentication.getName()));
    }

    // Get my bookings
    @GetMapping("/my")
    public ResponseEntity<Page<BookingResponse>> getMyBookings(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                bookingService.getMyBookings(authentication.getName(), page, size)
        );
    }

    // Get booking by id
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable String id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                bookingService.getBookingById(id, authentication.getName())
        );
    }

    // Get event bookings (organizer)
    @GetMapping("/event/{eventId}")
    public ResponseEntity<Page<BookingResponse>> getEventBookings(
            @PathVariable String eventId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                bookingService.getEventBookings(eventId, page, size)
        );
    }

    // Cancel booking
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable String id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                bookingService.cancelBooking(id, authentication.getName())
        );
    }
}