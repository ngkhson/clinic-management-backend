package com.clinic.booking.controller;

import com.clinic.booking.exception.AppException;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.dto.schedule.ScheduleGenerateRequest;
import com.clinic.booking.entity.Doctor;
import com.clinic.booking.entity.Schedule;
import com.clinic.booking.repository.DoctorRepository;
import com.clinic.booking.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/schedules")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminScheduleController {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    @PostMapping("/generate")
    @Transactional
    public ApiResponse<String> generateSchedules(@RequestBody ScheduleGenerateRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));

        int count = 0;
        for (String timeSlot : request.getTimeSlots()) {
            // Khởi tạo lịch khám.
            Schedule schedule = Schedule.builder()
                    .doctor(doctor)
                    .workDate(request.getDate())
                    .timeSlot(timeSlot)
                    .maxPatients(request.getMaxPatients() != null ? request.getMaxPatients() : 1)
                    .currentPatients(0)
                    .build();

            scheduleRepository.save(schedule);
            count++;
        }

        return ApiResponse.success("Đã tạo thành công " + count + " ca khám!");
    }
}