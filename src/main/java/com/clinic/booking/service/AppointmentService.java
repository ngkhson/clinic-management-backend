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

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User patient = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Schedule> availableSchedules = scheduleRepository.findAvailableSchedules(
                request.getSpecialtyId(), request.getAppointmentDate(), request.getTimeSlot());

        if (availableSchedules.isEmpty()) {
            throw new AppException(ErrorCode.SCHEDULE_FULL);
        }

        Schedule schedule = availableSchedules.get(0);
        Doctor doctor = schedule.getDoctor();

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

        return mapToDTO(appointment);
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

    @Transactional
    public AppointmentResponse updateAppointment(Long id, com.clinic.booking.dto.appointment.AppointmentUpdateRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        boolean wasCancelled = "CANCELLED".equals(appointment.getStatus()) || "NO_SHOW".equals(appointment.getStatus());
        boolean isNowCancelled = "CANCELLED".equals(request.getStatus()) || "NO_SHOW".equals(request.getStatus());

        if (request.getStatus() != null && !request.getStatus().equals(appointment.getStatus())) {
            appointment.setStatus(request.getStatus());
            
            // Nếu chuyển từ trạng thái bình thường sang CANCELLED/NO_SHOW -> Giảm currentPatients
            if (!wasCancelled && isNowCancelled) {
                Schedule schedule = appointment.getSchedule();
                if (schedule.getCurrentPatients() > 0) {
                    schedule.setCurrentPatients(schedule.getCurrentPatients() - 1);
                    scheduleRepository.save(schedule);
                }
            }
            // (Tuỳ chọn: Nếu từ CANCELLED khôi phục lại thì có thể phải cộng lên lại, nhưng thường người ta sẽ tạo mới hẹn)
        }

        if (request.getSymptoms() != null) {
            appointment.setSymptoms(request.getSymptoms());
        }

        appointmentRepository.save(appointment);
        return mapToDTO(appointment);
    }

    @Transactional
    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        if (!"CANCELLED".equals(appointment.getStatus()) && !"NO_SHOW".equals(appointment.getStatus())) {
            appointment.setStatus("CANCELLED");
            
            Schedule schedule = appointment.getSchedule();
            if (schedule.getCurrentPatients() > 0) {
                schedule.setCurrentPatients(schedule.getCurrentPatients() - 1);
                scheduleRepository.save(schedule);
            }
            appointmentRepository.save(appointment);
        }
    }
}