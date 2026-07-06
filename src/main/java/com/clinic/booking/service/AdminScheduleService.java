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
}
