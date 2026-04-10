package com.smartevent.event_service.repository;

import com.smartevent.event_service.entity.Event;
import com.smartevent.event_service.entity.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    // كل events الـ organizer
    Page<Event> findByOrganizerId(String organizerId, Pageable pageable);

    // كل events الـ published
    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    // Search by title
    Page<Event> findByTitleContainingIgnoreCaseAndStatus(
            String title,
            EventStatus status,
            Pageable pageable
    );
}