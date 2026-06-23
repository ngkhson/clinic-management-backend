package com.clinic.booking.service;

import com.clinic.booking.dto.AdminReceptionRequest;
import com.clinic.booking.dto.AppointmentDTO;
import com.clinic.booking.entity.Appointment;
import com.clinic.booking.entity.Doctor;
import com.clinic.booking.entity.Schedule;
import com.clinic.booking.entity.User;
import com.clinic.booking.entity.VitalSign;
import com.clinic.booking.repository.AppointmentRepository;
import com.clinic.booking.repository.DoctorRepository;
import com.clinic.booking.repository.ScheduleRepository;
import com.clinic.booking.repository.UserRepository;
import com.clinic.booking.repository.VitalSignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminReceptionService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final ScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final VitalSignRepository vitalSignRepository;

    @Transactional
    public AppointmentDTO createReceptionAndVitals(AdminReceptionRequest request) {
        User patient = userRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh nhân!"));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Bác sĩ!"));
        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Lịch khám!"));

        if (schedule.getCurrentPatients() >= schedule.getMaxPatients()) {
            throw new RuntimeException("Ca khám này đã đầy!");
        }

        // 1. Tạo Lịch hẹn (Vì Lễ tân tạo nên trạng thái mặc định là CONFIRMED - Đã xác nhận/Sắp khám)
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .schedule(schedule)
                .appointmentDate(schedule.getWorkDate())
                .status("CONFIRMED")
                .symptoms(request.getSymptoms())
                .build();
        appointment = appointmentRepository.save(appointment);

        // 2. Cập nhật số lượng bệnh nhân của ca khám
        schedule.setCurrentPatients(schedule.getCurrentPatients() + 1);
        scheduleRepository.save(schedule);

        // 3. Lưu Chỉ số sinh tồn (Nếu có nhập)
        if (request.getHeight() != null || request.getWeight() != null || request.getBloodPressure() != null) {
            VitalSign vitals = VitalSign.builder()
                    .appointment(appointment)
                    .pulse(request.getPulse())
                    .temperature(request.getTemperature())
                    .bloodPressure(request.getBloodPressure())
                    .respiratoryRate(request.getRespiratoryRate())
                    .height(request.getHeight())
                    .weight(request.getWeight())
                    .bmi(request.getBmi())
                    .notes(request.getNotes())
                    .build();
            vitalSignRepository.save(vitals);
        }

        return AppointmentDTO.builder()
                .id(appointment.getId())
                .patientName(patient.getFullName())
                .doctorName(doctor.getUser().getFullName())
                .timeSlot(schedule.getTimeSlot())
                .status(appointment.getStatus())
                .build();
    }
}