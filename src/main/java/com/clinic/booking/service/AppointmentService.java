package com.clinic.booking.service;

import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.dto.appointment.AppointmentRequest;
import com.clinic.booking.dto.appointment.AppointmentResponse;
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
    public AppointmentResponse createAppointment(AppointmentRequest request, String ipAddress) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User patient = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        if (schedule.getCurrentPatients() >= schedule.getMaxPatients()) {
            throw new AppException(ErrorCode.INVALID_ACTION);
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

        AppointmentResponse responseDTO = mapToDTO(appointment);

        // THÊM LOGIC THANH TOÁN VNPAY NẾU NGƯỜI DÙNG CHỌN "PAY_NOW"
        if ("PAY_NOW".equals(request.getPaymentType())) {
            // Lấy giá khám của Bác sĩ làm số tiền thanh toán
            double amount = doctor.getExaminationPrice() != null ? doctor.getExaminationPrice().doubleValue() : 0.0;
            // Gọi Service để sinh URL VNPAY
            String paymentUrl = paymentService.createVnPayUrl(appointment.getId(), amount, ipAddress);
            responseDTO.setPaymentType("PAY_NOW");
            responseDTO.setPaymentUrl(paymentUrl);
        } else {
            responseDTO.setPaymentType("PAY_LATER");
        }

        return responseDTO;
    }

    private AppointmentResponse mapToDTO(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getUser().getFullName())
                .patientName(appointment.getPatient().getFullName())
                .scheduleId(appointment.getSchedule().getId())
                .timeSlot(appointment.getSchedule().getTimeSlot())
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .symptoms(appointment.getSymptoms())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}