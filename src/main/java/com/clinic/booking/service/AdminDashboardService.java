package com.clinic.booking.service;

import com.clinic.booking.dto.AppointmentDTO;
import com.clinic.booking.entity.Appointment;
import com.clinic.booking.repository.AppointmentRepository;
import com.clinic.booking.repository.DoctorRepository;
import com.clinic.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    public Map<String, Object> getDashboardStats() {
        List<Appointment> allAppointments = appointmentRepository.findAll();

        long totalDoctors = doctorRepository.count();
        // Lọc ra các user có role PATIENT
        long totalPatients = userRepository.findAll().stream()
                .filter(u -> "PATIENT".equals(u.getRole()))
                .count();
        long totalAppointments = allAppointments.size();

        // Tính doanh thu từ các lịch hẹn đã hoàn thành (COMPLETED)
        BigDecimal totalRevenue = allAppointments.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()))
                .map(a -> a.getDoctor().getExaminationPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingAppointments = allAppointments.stream()
                .filter(a -> "PENDING".equals(a.getStatus()))
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDoctors", totalDoctors);
        stats.put("totalPatients", totalPatients);
        stats.put("totalAppointments", totalAppointments);
        stats.put("totalRevenue", totalRevenue);
        stats.put("pendingAppointments", pendingAppointments);

        return stats;
    }

    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream().map(app -> AppointmentDTO.builder()
                .id(app.getId())
                .doctorId(app.getDoctor().getId())
                .doctorName(app.getDoctor().getUser().getFullName())
                .patientName(app.getPatient().getFullName()) // Trả về tên Bệnh nhân
                .scheduleId(app.getSchedule().getId())
                .timeSlot(app.getSchedule().getTimeSlot())
                .appointmentDate(app.getAppointmentDate())
                .status(app.getStatus())
                .symptoms(app.getSymptoms())
                .createdAt(app.getCreatedAt())
                .build()).collect(Collectors.toList());
    }
}