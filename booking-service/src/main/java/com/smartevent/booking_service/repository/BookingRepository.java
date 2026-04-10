package com.smartevent.booking_service.repository;


import com.smartevent.booking_service.entity.Booking;
import com.smartevent.booking_service.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    Page<Booking> findByAttendeeId(String attendeeId, Pageable pageable);

    Page<Booking> findByEventId(String eventId, Pageable pageable);

    boolean existsByEventIdAndAttendeeIdAndStatusNot(
            String eventId,
            String attendeeId,
            BookingStatus status
    );
}