package com.clinic.booking.service;

import com.clinic.booking.entity.Appointment;
import com.clinic.booking.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentStatusScheduler {

    private final AppointmentRepository appointmentRepository;

    // Chạy định kỳ mỗi phút
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateNoShowAppointments() {
        List<Appointment> pendingAppointments = appointmentRepository.findByStatus("PENDING");
        LocalDateTime now = LocalDateTime.now();

        for (Appointment appointment : pendingAppointments) {
            try {
                LocalDate date = appointment.getAppointmentDate();
                String timeSlot = appointment.getSchedule().getTimeSlot(); // e.g. "08:00"

                if (date != null && timeSlot != null) {
                    LocalTime time = LocalTime.parse(timeSlot);
                    LocalDateTime appointmentDateTime = LocalDateTime.of(date, time);

                    // Quá 15 phút so với giờ hẹn
                    if (now.isAfter(appointmentDateTime.plusMinutes(15))) {
                        log.info("Cập nhật trạng thái NO_SHOW cho Appointment ID: {}", appointment.getId());
                        appointment.setStatus("NO_SHOW"); // Hoặc CANCELLED tuỳ yêu cầu
                        appointmentRepository.save(appointment);
                    }
                }
            } catch (DateTimeParseException e) {
                log.error("Lỗi parse thời gian cho Appointment ID: {}", appointment.getId(), e);
            }
        }
    }
}
