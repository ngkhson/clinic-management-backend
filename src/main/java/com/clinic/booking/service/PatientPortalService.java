package com.clinic.booking.service;

import com.clinic.booking.dto.AppointmentDTO;
import com.clinic.booking.dto.MedicalRecordDTO;
import com.clinic.booking.dto.PrescriptionDetailDTO;
import com.clinic.booking.entity.Appointment;
import com.clinic.booking.entity.MedicalRecord;
import com.clinic.booking.entity.MedicalService;
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

    private User getCurrentPatient() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Không tìm thấy Bệnh nhân!"));
    }

    public List<AppointmentDTO> getMyAppointments() {
        User currentPatient = getCurrentPatient();
        return appointmentRepository.findByPatientId(currentPatient.getId())
                .stream()
                .map(app -> AppointmentDTO.builder()
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
                        .build())
                .collect(Collectors.toList());
    }

    public MedicalRecordDTO getMedicalRecord(Long appointmentId) {
        User currentPatient = getCurrentPatient();
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Lịch hẹn!"));

        if (!appointment.getPatient().getId().equals(currentPatient.getId())) {
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
                // LẤY TÊN CÁC DỊCH VỤ ĐÃ CHỈ ĐỊNH (MODULE 3)
                .serviceNames(record.getServices() != null ? record.getServices().stream().map(MedicalService::getName).collect(Collectors.toList()) : List.of())
                // LẤY CHI TIẾT TOA THUỐC VỀ CHO BỆNH NHÂN (MODULE 5)
                .prescriptionDetails(record.getPrescriptionDetails() != null ?
                        record.getPrescriptionDetails().stream().map(d -> PrescriptionDetailDTO.builder()
                                .id(d.getId())
                                .medicineId(d.getMedicine().getId())
                                .medicineName(d.getMedicine().getName())
                                .unit(d.getMedicine().getUnit())
                                .quantity(d.getQuantity())
                                .dosageInstruction(d.getDosageInstruction())
                                .build()).collect(Collectors.toList()) : List.of())
                .createdAt(record.getCreatedAt())
                .build();
    }
}