package com.clinic.booking.repository;

import com.clinic.booking.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    // Tìm hồ sơ bệnh án dựa vào ID của lịch hẹn
    Optional<MedicalRecord> findByAppointmentId(Long appointmentId);
}