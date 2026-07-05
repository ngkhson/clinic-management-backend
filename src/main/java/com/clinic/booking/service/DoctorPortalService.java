package com.clinic.booking.service;

import com.clinic.booking.dto.appointment.AppointmentResponse;
import com.clinic.booking.dto.record.MedicalRecordResponse;
import com.clinic.booking.dto.pharmacy.PrescriptionDetailResponse;
import com.clinic.booking.entity.*;
import com.clinic.booking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorPortalService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final MedicineRepository medicineRepository;
    private final NotificationService notificationService;

    private Doctor getCurrentDoctor() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Không tìm thấy User!"));
        return doctorRepository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("Tài khoản này không phải là Bác sĩ!"));
    }

    public List<AppointmentResponse> getDoctorAppointments(String status) {
        Doctor currentDoctor = getCurrentDoctor();
        List<Appointment> appointments;
        if (status != null && !status.isEmpty()) {
            appointments = appointmentRepository.findByDoctorIdAndStatus(currentDoctor.getId(), status);
        } else {
            appointments = appointmentRepository.findByDoctorId(currentDoctor.getId());
        }

        return appointments.stream().map(app -> AppointmentResponse.builder()
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
                .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    public void updateAppointmentStatus(Long appointmentId, String newStatus) {
        Doctor currentDoctor = getCurrentDoctor();
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();

        if (!appointment.getDoctor().getId().equals(currentDoctor.getId())) {
            throw new RuntimeException("Bạn không có quyền cập nhật lịch hẹn này!");
        }

        appointment.setStatus(newStatus.toUpperCase());
        appointmentRepository.save(appointment);
    }

    public MedicalRecordResponse getDraftRecord(Long appointmentId) {
        return medicalRecordRepository.findByAppointmentId(appointmentId)
                .map(this::mapToDTO)
                .orElse(null);
    }

    @Transactional
    public MedicalRecordResponse saveMedicalRecord(MedicalRecordResponse request) {
        Doctor currentDoctor = getCurrentDoctor();
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId()).orElseThrow();

        if (!appointment.getDoctor().getId().equals(currentDoctor.getId())) {
            throw new RuntimeException("Bạn không có quyền cập nhật bệnh án này!");
        }

        MedicalRecord record = medicalRecordRepository.findByAppointmentId(appointment.getId())
                .orElse(new MedicalRecord());

        record.setAppointment(appointment);

        // --- LƯU CHỈ SỐ SINH HIỆU ---
        record.setPulse(request.getPulse());
        record.setTemp(request.getTemp());
        record.setBp(request.getBp());
        record.setResp(request.getResp());
        record.setHeight(request.getHeight());
        record.setWeight(request.getWeight());

        // Lưu thông tin lâm sàng & Dặn dò
        record.setMedicalHistory(request.getMedicalHistory());
        record.setAllergies(request.getAllergies());
        record.setReasonForVisit(request.getReasonForVisit());
        record.setIllnessHistory(request.getIllnessHistory());
        record.setClinicalSymptoms(request.getClinicalSymptoms());
        record.setParaclinicalResults(request.getParaclinicalResults());
        record.setDiagnosis(request.getDiagnosis());
        record.setTreatmentPlan(request.getTreatmentPlan());
        record.setPrescription(request.getPrescription());
        record.setNotes(request.getNotes());
        record.setFollowUpDate(request.getFollowUpDate());

        // Cập nhật dịch vụ
        if (request.getServiceIds() != null && !request.getServiceIds().isEmpty()) {
            record.setServices(medicalServiceRepository.findAllById(request.getServiceIds()));
        } else {
            record.setServices(new ArrayList<>());
        }

        // Cập nhật đơn thuốc
        if (record.getPrescriptionDetails() == null) {
            record.setPrescriptionDetails(new ArrayList<>());
        } else {
            record.getPrescriptionDetails().clear();
        }

        if (request.getPrescriptionDetails() != null) {
            for (var dto : request.getPrescriptionDetails()) {
                Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc: " + dto.getMedicineId()));

                if (!request.isDraft() && !"COMPLETED".equals(appointment.getStatus())) {
                    int currentQty = medicine.getCurrentQuantity() != null ? medicine.getCurrentQuantity() : 0;
                    if (currentQty < dto.getQuantity()) {
                        throw new RuntimeException("Thuốc " + medicine.getName() + " không đủ tồn kho!");
                    }
                    medicine.setCurrentQuantity(currentQty - dto.getQuantity());
                    medicineRepository.save(medicine);
                }

                PrescriptionDetail detail = PrescriptionDetail.builder()
                        .medicalRecord(record)
                        .medicine(medicine)
                        .quantity(dto.getQuantity())
                        .dosageInstruction(dto.getDosageInstruction())
                        .build();
                record.getPrescriptionDetails().add(detail);
            }
        }

        record = medicalRecordRepository.save(record);

        if (request.isDraft()) {
            appointment.setStatus("EXAMINING");
        } else {
            appointment.setStatus("COMPLETED");
            notificationService.sendNotification(
                    appointment.getPatient(),
                    "Bác sĩ " + currentDoctor.getUser().getFullName() + " đã cập nhật Hồ sơ bệnh án của bạn. Vui lòng xem chi tiết trong phần Hồ sơ của tôi."
            );
        }
        appointmentRepository.save(appointment);

        return mapToDTO(record);
    }

    private MedicalRecordResponse mapToDTO(MedicalRecord record) {
        return MedicalRecordResponse.builder()
                .id(record.getId())
                .appointmentId(record.getAppointment().getId())
                .patientName(record.getAppointment().getPatient().getFullName())

                // --- MAP CHỈ SỐ SINH HIỆU TRẢ VỀ FRONT-END ---
                .pulse(record.getPulse())
                .temp(record.getTemp())
                .bp(record.getBp())
                .resp(record.getResp())
                .height(record.getHeight())
                .weight(record.getWeight())

                .medicalHistory(record.getMedicalHistory())
                .allergies(record.getAllergies())
                .reasonForVisit(record.getReasonForVisit())
                .illnessHistory(record.getIllnessHistory())
                .clinicalSymptoms(record.getClinicalSymptoms())
                .paraclinicalResults(record.getParaclinicalResults())
                .diagnosis(record.getDiagnosis())
                .treatmentPlan(record.getTreatmentPlan())
                .prescription(record.getPrescription())
                .notes(record.getNotes())
                .followUpDate(record.getFollowUpDate())
                .serviceIds(record.getServices() != null ? record.getServices().stream().map(MedicalService::getId).collect(Collectors.toList()) : List.of())
                .prescriptionDetails(record.getPrescriptionDetails() != null ? record.getPrescriptionDetails().stream().map(d -> PrescriptionDetailResponse.builder()
                        .medicineId(d.getMedicine().getId())
                        .medicineName(d.getMedicine().getName())
                        .unit(d.getMedicine().getUnit())
                        .quantity(d.getQuantity())
                        .dosageInstruction(d.getDosageInstruction())
                        .build()).collect(Collectors.toList()) : List.of())
                .build();
    }
}