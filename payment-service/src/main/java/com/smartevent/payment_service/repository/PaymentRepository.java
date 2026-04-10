package com.smartevent.payment_service.repository;

import com.smartevent.payment_service.entity.Payment;
import com.smartevent.payment_service.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByBookingId(String bookingId);

    List<Payment> findByUserId(String userId);

    Optional<Payment> findByBookingIdAndStatus(String bookingId, PaymentStatus status);
}
