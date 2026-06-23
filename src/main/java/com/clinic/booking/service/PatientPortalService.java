package com.clinic.booking.service;

import com.clinic.booking.dto.AppointmentDTO;
import com.clinic.booking.dto.MedicalRecordDTO;
import com.clinic.booking.entity.Appointment;
import com.clinic.booking.entity.MedicalRecord;
import com.clinic.booking.entity.MedicalService; // THÊM DÒNG NÀY
import com.clinic.booking.entity.User;
import com.clinic.booking.repository.AppointmentRepository;
import com.clinic.booking.repository.MedicalRecordRepository;
import com.clinic.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientPortalService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User!"));
    }

    // 1. Lấy danh sách lịch hẹn của bệnh nhân đang đăng nhập
    public List<AppointmentDTO> getMyAppointments() {
        User currentUser = getCurrentUser();

        // Sửa lại thành findByPatientId vì patient chính là User
        List<Appointment> appointments = appointmentRepository.findByPatientId(currentUser.getId());

        return appointments.stream().map(app -> AppointmentDTO.builder()
                .id(app.getId())
                .doctorId(app.getDoctor().getId())
                .doctorName(app.getDoctor().getUser().getFullName())
                .scheduleId(app.getSchedule().getId())
                .timeSlot(app.getSchedule().getTimeSlot())
                .appointmentDate(app.getAppointmentDate())
                .status(app.getStatus())
                .symptoms(app.getSymptoms())
                .createdAt(app.getCreatedAt())
                .build()
        ).collect(Collectors.toList());
    }

    // 2. Lấy hồ sơ bệnh án của một lịch hẹn cụ thể
    public MedicalRecordDTO getMedicalRecord(Long appointmentId) {
        User currentUser = getCurrentUser();

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn!"));

        // Bảo mật: Sửa lỗi gọi thẳng getId() vì patient đã là User
        if (!appointment.getPatient().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền xem bệnh án này!");
        }

        MedicalRecord record = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn này chưa có hoặc chưa cập nhật hồ sơ bệnh án!"));

        return MedicalRecordDTO.builder()
                .id(record.getId())
                .appointmentId(appointment.getId())
                .patientName(appointment.getPatient().getFullName())
                .diagnosis(record.getDiagnosis())
                .treatmentPlan(record.getTreatmentPlan())
                .prescription(record.getPrescription())
                .notes(record.getNotes())
                // THÊM DÒNG NÀY ĐỂ LẤY TÊN CÁC DỊCH VỤ ĐÃ CHỈ ĐỊNH
                .serviceNames(record.getServices() != null ? record.getServices().stream().map(MedicalService::getName).collect(Collectors.toList()) : List.of())
                .createdAt(record.getCreatedAt())
                .build();
    }
}