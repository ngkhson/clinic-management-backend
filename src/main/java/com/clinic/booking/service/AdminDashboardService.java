package com.clinic.booking.service;

import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.dto.appointment.AppointmentResponse;
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
    private final com.clinic.booking.repository.InvoiceRepository invoiceRepository;
    private final NotificationService notificationService;

    public Map<String, Object> getDashboardStats() {
        List<Appointment> allAppointments = appointmentRepository.findAll();

        long totalDoctors = doctorRepository.count();
        // Lọc ra các user có role PATIENT
        long totalPatients = userRepository.findAll().stream()
                .filter(u -> u.getRoles() != null && u.getRoles().stream().anyMatch(r -> "PATIENT".equals(r.getName())))
                .count();
        long totalAppointments = allAppointments.size();

        // Tính doanh thu từ bảng Invoice
        double totalRev = invoiceRepository.findAll().stream()
                .filter(i -> "PAID".equals(i.getStatus()))
                .mapToDouble(i -> i.getTotalAmount() != null ? i.getTotalAmount() : 0.0)
                .sum();
        BigDecimal totalRevenue = BigDecimal.valueOf(totalRev);

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

    // THÊM HÀM NÀY ĐỂ ADMIN CẬP NHẬT TRẠNG THÁI LỊCH HẸN
    public void updateAppointmentStatus(Long appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));
        appointment.setStatus(status);
        appointmentRepository.save(appointment);

        if ("CONFIRMED".equals(status)) {
            notificationService.sendNotification(
                    appointment.getPatient(),
                    "Lịch hẹn của bạn vào ngày " + appointment.getAppointmentDate() + " lúc " + appointment.getSchedule().getTimeSlot() + " đã được xác nhận. Vui lòng đến đúng giờ!"
            );
        } else if ("CANCELLED".equals(status)) {
            notificationService.sendNotification(
                    appointment.getPatient(),
                    "Lịch hẹn của bạn vào ngày " + appointment.getAppointmentDate() + " đã bị huỷ bởi Quản trị viên."
            );
        } else if ("NO_SHOW".equals(status)) {
            notificationService.sendNotification(
                    appointment.getPatient(),
                    "Bạn đã không đến khám theo lịch hẹn ngày " + appointment.getAppointmentDate() + ". Lịch hẹn đã bị huỷ."
            );
        }
    }

    public org.springframework.data.domain.Page<AppointmentResponse> getAppointments(int page, int size, String search, String status) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by("id").descending());
        String searchTerm = (search == null || search.trim().isEmpty()) ? null : search.trim();
        String statusTerm = (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status)) ? null : status.trim();

        return appointmentRepository.findAppointments(searchTerm, statusTerm, pageable).map(app -> AppointmentResponse.builder()
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
                .build());
    }

    public List<AppointmentResponse> getAllAppointmentsList() {
        return appointmentRepository.findAll().stream().map(app -> AppointmentResponse.builder()
                .id(app.getId())
                .doctorId(app.getDoctor().getId())
                .doctorName(app.getDoctor().getUser().getFullName())
                .patientName(app.getPatient().getFullName())
                .scheduleId(app.getSchedule().getId())
                .timeSlot(app.getSchedule().getTimeSlot())
                .appointmentDate(app.getAppointmentDate())
                .status(app.getStatus())
                .symptoms(app.getSymptoms())
                .createdAt(app.getCreatedAt())
                .build()).collect(java.util.stream.Collectors.toList());
    }
}