package com.clinic.booking.service;

import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.dto.appointment.AppointmentResponse;
import com.clinic.booking.dto.record.MedicalRecordResponse;
import com.clinic.booking.dto.pharmacy.PrescriptionDetailResponse;
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
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public List<AppointmentResponse> getMyAppointments() {
        User currentPatient = getCurrentPatient();
        return appointmentRepository.findByPatientId(currentPatient.getId())
                .stream()
                .map(app -> AppointmentResponse.builder()
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

    public MedicalRecordResponse getMedicalRecord(Long appointmentId) {
        User currentPatient = getCurrentPatient();
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        if (!appointment.getPatient().getId().equals(currentPatient.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        MedicalRecord record = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_ACTION));

        return MedicalRecordResponse.builder()
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
                        record.getPrescriptionDetails().stream().map(d -> PrescriptionDetailResponse.builder()
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