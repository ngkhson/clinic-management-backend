package com.clinic.booking.controller;

import com.clinic.booking.dto.ScheduleGenerateRequest;
import com.clinic.booking.entity.Doctor;
import com.clinic.booking.entity.Schedule;
import com.clinic.booking.repository.DoctorRepository;
import com.clinic.booking.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin/schedules")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminScheduleController {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    @PostMapping("/generate")
    @Transactional
    public ResponseEntity<String> generateSchedules(@RequestBody ScheduleGenerateRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ!"));

        int count = 0;
        for (String timeSlot : request.getTimeSlots()) {
            // Khởi tạo lịch khám.
            // LƯU Ý: Nếu trong Entity Schedule của bạn trường ngày tên là 'scheduleDate' thay vì 'date', hãy đổi lại cho khớp nhé!
            Schedule schedule = Schedule.builder()
                    .doctor(doctor)
                    .workDate(request.getDate()) // Đảm bảo tên trường khớp với Entity Schedule của bạn
                    .timeSlot(timeSlot)
                    .maxPatients(request.getMaxPatients() != null ? request.getMaxPatients() : 1)
                    .currentPatients(0)
                    .build();

            scheduleRepository.save(schedule);
            count++;
        }

        return ResponseEntity.ok("Đã tạo thành công " + count + " ca khám!");
    }
}