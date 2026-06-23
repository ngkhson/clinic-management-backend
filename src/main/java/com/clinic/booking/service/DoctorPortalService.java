package com.clinic.booking.service;

import com.clinic.booking.dto.AppointmentDTO;
import com.clinic.booking.dto.MedicalRecordDTO;
import com.clinic.booking.entity.Appointment;
import com.clinic.booking.entity.Doctor;
import com.clinic.booking.entity.MedicalRecord;
import com.clinic.booking.entity.MedicalService;
import com.clinic.booking.entity.Medicine;
import com.clinic.booking.entity.PrescriptionDetail;
import com.clinic.booking.entity.User;
import com.clinic.booking.repository.AppointmentRepository;
import com.clinic.booking.repository.DoctorRepository;
import com.clinic.booking.repository.MedicalRecordRepository;
import com.clinic.booking.repository.MedicalServiceRepository;
import com.clinic.booking.repository.MedicineRepository;
import com.clinic.booking.repository.UserRepository;
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
    private final MedicineRepository medicineRepository; // THÊM REPOSITORY KHO THUỐC
    private final NotificationService notificationService;

    private Doctor getCurrentDoctor() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Không tìm thấy User!"));
        return doctorRepository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("Tài khoản này không phải là Bác sĩ!"));
    }

    public List<AppointmentDTO> getDoctorAppointments(String status) {
        Doctor currentDoctor = getCurrentDoctor();
        List<Appointment> appointments;
        if (status != null && !status.isEmpty()) {
            appointments = appointmentRepository.findByDoctorIdAndStatus(currentDoctor.getId(), status);
        } else {
            appointments = appointmentRepository.findByDoctorId(currentDoctor.getId());
        }

        return appointments.stream().map(app -> AppointmentDTO.builder()
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

        String statusVN = newStatus.equals("CONFIRMED") ? "Đã xác nhận" : newStatus.equals("CANCELLED") ? "Bị hủy" : newStatus;
        notificationService.sendNotification(
                appointment.getPatient(),
                "Lịch hẹn khám của bạn vào ngày " + appointment.getAppointmentDate() + " đã được Bác sĩ chuyển sang trạng thái: " + statusVN + "."
        );
    }

    // LƯU Ý @Transactional RẤT QUAN TRỌNG: NẾU TRỪ KHO LỖI (VÍ DỤ HẾT THUỐC), TOÀN BỘ GIAO DỊCH SẼ BỊ HỦY BỎ (ROLLBACK)
    @Transactional
    public MedicalRecordDTO createMedicalRecord(MedicalRecordDTO request) {
        Doctor currentDoctor = getCurrentDoctor();
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId()).orElseThrow();

        if (!appointment.getDoctor().getId().equals(currentDoctor.getId())) {
            throw new RuntimeException("Bạn không có quyền tạo bệnh án!");
        }
        if (medicalRecordRepository.findByAppointmentId(appointment.getId()).isPresent()) {
            throw new RuntimeException("Lịch hẹn này đã có hồ sơ bệnh án!");
        }

        // Lấy danh sách dịch vụ bác sĩ đã tick chọn
        List<MedicalService> assignedServices = new ArrayList<>();
        if (request.getServiceIds() != null && !request.getServiceIds().isEmpty()) {
            assignedServices = medicalServiceRepository.findAllById(request.getServiceIds());
        }

        // Khởi tạo Bệnh án
        MedicalRecord record = MedicalRecord.builder()
                .appointment(appointment)
                .diagnosis(request.getDiagnosis())
                .treatmentPlan(request.getTreatmentPlan())
                .prescription(request.getPrescription()) // Thuốc ngoài / Thuốc tự túc (Nhập tay)
                .notes(request.getNotes())
                .services(assignedServices)
                .build();

        // THÊM MỚI: XỬ LÝ ĐƠN THUỐC ĐIỆN TỬ VÀ TRỪ KHO TỰ ĐỘNG
        List<PrescriptionDetail> details = new ArrayList<>();
        if (request.getPrescriptionDetails() != null && !request.getPrescriptionDetails().isEmpty()) {
            for (var dto : request.getPrescriptionDetails()) {
                Medicine medicine = medicineRepository.findById(dto.getMedicineId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc ID: " + dto.getMedicineId()));

                int currentQty = medicine.getCurrentQuantity() != null ? medicine.getCurrentQuantity() : 0;

                // Kiểm tra xem số lượng trong kho có đủ để Bác sĩ kê không
                if (currentQty < dto.getQuantity()) {
                    throw new RuntimeException("Thuốc " + medicine.getName() + " không đủ tồn kho. Hiện chỉ còn: " + currentQty + " " + medicine.getUnit());
                }

                // Tự động trừ tồn kho
                medicine.setCurrentQuantity(currentQty - dto.getQuantity());
                medicineRepository.save(medicine);

                // Tạo chi tiết toa thuốc
                PrescriptionDetail detail = PrescriptionDetail.builder()
                        .medicalRecord(record)
                        .medicine(medicine)
                        .quantity(dto.getQuantity())
                        .dosageInstruction(dto.getDosageInstruction())
                        .build();
                details.add(detail);
            }
        }

        // Liên kết đơn thuốc vào bệnh án (Do cascade = CascadeType.ALL, khi save record nó sẽ tự lưu các chi tiết này)
        record.setPrescriptionDetails(details);

        // Lưu toàn bộ thông tin bệnh án, dịch vụ và đơn thuốc xuống DB
        record = medicalRecordRepository.save(record);

        // Chuyển trạng thái lịch hẹn thành Đã xong
        appointment.setStatus("COMPLETED");
        appointmentRepository.save(appointment);

        // Bắn thông báo cho bệnh nhân
        notificationService.sendNotification(
                appointment.getPatient(),
                "Bác sĩ " + currentDoctor.getUser().getFullName() + " đã cập nhật Hồ sơ bệnh án và Kê đơn thuốc cho ca khám ngày " + appointment.getAppointmentDate() + ". Bạn có thể in đơn thuốc ngay!"
        );

        return MedicalRecordDTO.builder()
                .id(record.getId())
                .appointmentId(appointment.getId())
                .patientName(appointment.getPatient().getFullName())
                .diagnosis(record.getDiagnosis())
                .treatmentPlan(record.getTreatmentPlan())
                .prescription(record.getPrescription())
                .notes(record.getNotes())
                .serviceNames(assignedServices.stream().map(MedicalService::getName).collect(Collectors.toList()))
                .createdAt(record.getCreatedAt())
                .build();
    }
}