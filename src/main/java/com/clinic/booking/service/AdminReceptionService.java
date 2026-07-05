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
        Appointment appointment;
        User patient;
        Doctor doctor;
        String timeSlotStr;

        // KIỂM TRA: LÀ KHÁCH ĐẶT TRƯỚC HAY VÃNG LAI?
        if (request.getAppointmentId() != null) {
            // 1. KHÁCH ĐÃ ĐẶT LỊCH TRƯỚC -> Dùng lại lịch cũ
            appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Lịch hẹn!"));

            appointment.setStatus("CONFIRMED"); // Lễ tân xác nhận khách đã đến

            // Cập nhật thêm triệu chứng nếu Lễ tân có gõ thêm
            if (request.getSymptoms() != null && !request.getSymptoms().isEmpty()) {
                appointment.setSymptoms(request.getSymptoms());
            }
            appointment = appointmentRepository.save(appointment);

            patient = appointment.getPatient();
            doctor = appointment.getDoctor();
            timeSlotStr = appointment.getSchedule().getTimeSlot();

        } else {
            // 2. KHÁCH VÃNG LAI -> Tạo lịch mới hoàn toàn
            patient = userRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh nhân!"));
            doctor = doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Bác sĩ!"));
            Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Lịch khám!"));

            if (schedule.getCurrentPatients() >= schedule.getMaxPatients()) {
                throw new RuntimeException("Ca khám này đã đầy!");
            }

            appointment = Appointment.builder()
                    .patient(patient)
                    .doctor(doctor)
                    .schedule(schedule)
                    .appointmentDate(schedule.getWorkDate())
                    .status("CONFIRMED")
                    .symptoms(request.getSymptoms())
                    .build();
            appointment = appointmentRepository.save(appointment);

            schedule.setCurrentPatients(schedule.getCurrentPatients() + 1);
            scheduleRepository.save(schedule);

            timeSlotStr = schedule.getTimeSlot();
        }

        // 3. LƯU CHỈ SỐ SINH TỒN (Chung cho cả 2 trường hợp)
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
                .timeSlot(timeSlotStr)
                .status(appointment.getStatus())
                .build();
    }
}