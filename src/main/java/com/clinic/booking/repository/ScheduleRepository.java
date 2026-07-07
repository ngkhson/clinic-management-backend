package com.clinic.booking.repository;

import com.clinic.booking.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // Tìm tất cả lịch làm việc của một bác sĩ
    List<Schedule> findByDoctorId(Long doctorId);

    // Tìm các khung giờ của một bác sĩ trong đúng 1 ngày cụ thể
    List<Schedule> findByDoctorIdAndWorkDate(Long doctorId, LocalDate workDate);

    // Kiểm tra xem bác sĩ đã tạo lịch cho ca này trong ngày này chưa (tránh tạo trùng)
    boolean existsByDoctorIdAndWorkDateAndTimeSlot(Long doctorId, LocalDate workDate, String timeSlot);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM Schedule s JOIN s.doctor d WHERE d.specialty.id = :specialtyId " +
           "AND s.workDate = :workDate AND s.timeSlot = :timeSlot AND s.currentPatients < s.maxPatients")
    List<Schedule> findAvailableSchedules(@org.springframework.data.repository.query.Param("specialtyId") Long specialtyId, 
                                          @org.springframework.data.repository.query.Param("workDate") LocalDate workDate, 
                                          @org.springframework.data.repository.query.Param("timeSlot") String timeSlot);
}