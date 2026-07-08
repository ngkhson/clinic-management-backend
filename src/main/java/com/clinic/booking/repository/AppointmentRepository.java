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

    // Lọc theo trạng thái cho toàn hệ thống
    List<Appointment> findByStatus(String status);

    // Xoá tất cả lịch hẹn theo scheduleId
    void deleteByScheduleId(Long scheduleId);

    @org.springframework.data.jpa.repository.Query("SELECT a FROM Appointment a " +
           "JOIN a.patient p " +
           "JOIN a.doctor d " +
           "JOIN d.user du " +
           "WHERE (:searchTerm IS NULL OR " +
           "  LOWER(p.fullName) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%')) " +
           "  OR LOWER(du.fullName) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%')) " +
           "  OR str(a.id) LIKE CONCAT('%', CAST(:searchTerm AS string), '%')) " +
           "AND (:status IS NULL OR a.status = :status)")
    org.springframework.data.domain.Page<Appointment> findAppointments(
            @org.springframework.data.repository.query.Param("searchTerm") String searchTerm,
            @org.springframework.data.repository.query.Param("status") String status,
            org.springframework.data.domain.Pageable pageable);
}