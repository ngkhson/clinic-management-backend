package com.clinic.booking.service;

import com.clinic.booking.dto.AppointmentDTO;
import com.clinic.booking.entity.Appointment;
import com.clinic.booking.entity.Doctor;
import com.clinic.booking.entity.Schedule;
import com.clinic.booking.entity.User;
import com.clinic.booking.repository.AppointmentRepository;
import com.clinic.booking.repository.DoctorRepository;
import com.clinic.booking.repository.ScheduleRepository;
import com.clinic.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    // THÊM: Inject PaymentService
    private final PaymentService paymentService;

    @Transactional
    public AppointmentDTO createAppointment(AppointmentDTO request, String ipAddress) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User patient = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Bác sĩ!"));

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Lịch khám!"));

        if (schedule.getCurrentPatients() >= schedule.getMaxPatients()) {
            throw new RuntimeException("Xin lỗi, ca khám này đã nhận đủ số lượng bệnh nhân!");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .schedule(schedule)
                .appointmentDate(schedule.getWorkDate())
                .status("PENDING")
                .symptoms(request.getSymptoms())
                .build();

        appointment = appointmentRepository.save(appointment);

        schedule.setCurrentPatients(schedule.getCurrentPatients() + 1);
        scheduleRepository.save(schedule);

        AppointmentDTO responseDTO = mapToDTO(appointment);

        // THÊM LOGIC THANH TOÁN VNPAY NẾU NGƯỜI DÙNG CHỌN "PAY_NOW"
        if ("PAY_NOW".equals(request.getPaymentType())) {
            // Lấy giá khám của Bác sĩ làm số tiền thanh toán
            double amount = doctor.getExaminationPrice() != null ? doctor.getExaminationPrice().doubleValue() : 0.0;
            // Gọi Service để sinh URL VNPAY
            String paymentUrl = paymentService.createVnPayUrl(appointment.getId(), amount, ipAddress);
            responseDTO.setPaymentUrl(paymentUrl);
        }

        return responseDTO;
    }

    private AppointmentDTO mapToDTO(Appointment appointment) {
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getUser().getFullName())
                .scheduleId(appointment.getSchedule().getId())
                .timeSlot(appointment.getSchedule().getTimeSlot())
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .symptoms(appointment.getSymptoms())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}