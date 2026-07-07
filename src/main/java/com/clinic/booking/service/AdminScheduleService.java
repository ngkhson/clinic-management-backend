package com.clinic.booking.service;

import com.clinic.booking.dto.schedule.ScheduleGenerateRequest;
import com.clinic.booking.dto.schedule.ScheduleSlotRequest;
import com.clinic.booking.entity.Doctor;
import com.clinic.booking.entity.Schedule;
import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;
import com.clinic.booking.repository.DoctorRepository;
import com.clinic.booking.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    public int generateSchedules(ScheduleGenerateRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));

        int count = 0;
        for (ScheduleSlotRequest slot : request.getSlots()) {
            Schedule schedule = Schedule.builder()
                    .doctor(doctor)
                    .workDate(request.getDate())
                    .timeSlot(slot.getTimeSlot())
                    .maxPatients(slot.getMaxPatients() != null ? slot.getMaxPatients() : 1)
                    .currentPatients(0)
                    .build();

            scheduleRepository.save(schedule);
            count++;
        }

        return count;
    }

    @Transactional
    public void updateSchedule(Long id, com.clinic.booking.dto.schedule.ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (request.getMaxPatients() != null && request.getMaxPatients() < schedule.getCurrentPatients()) {
            throw new AppException(ErrorCode.INVALID_ACTION); // Không thể giảm maxPatients xuống dưới số lượng đã đặt
        }

        if (request.getWorkDate() != null) schedule.setWorkDate(request.getWorkDate());
        if (request.getTimeSlot() != null) schedule.setTimeSlot(request.getTimeSlot());
        if (request.getMaxPatients() != null) schedule.setMaxPatients(request.getMaxPatients());

        scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (schedule.getCurrentPatients() > 0) {
            throw new AppException(ErrorCode.INVALID_ACTION); // Đã có bệnh nhân đặt
        }

        scheduleRepository.delete(schedule);
    }
}
