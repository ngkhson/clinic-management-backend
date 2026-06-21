package com.clinic.booking.repository;

import com.clinic.booking.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Dành cho Bệnh nhân: Lấy toàn bộ lịch sử đặt khám của họ
    List<Appointment> findByPatientId(Long userId);

    // Dành cho Bác sĩ: Lấy danh sách các lịch hẹn của bác sĩ đó
    List<Appointment> findByDoctorId(Long doctorId);

    // Dành cho Bác sĩ: Lọc danh sách bệnh nhân theo trạng thái (VD: chỉ lấy các ca 'PENDING')
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, String status);
}