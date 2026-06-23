package com.clinic.booking.repository;

import com.clinic.booking.entity.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {
    Optional<VitalSign> findByAppointmentId(Long appointmentId);
}