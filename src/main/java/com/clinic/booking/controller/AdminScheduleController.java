package com.clinic.booking.controller;

import com.clinic.booking.exception.AppException;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.dto.schedule.ScheduleGenerateRequest;
import com.clinic.booking.service.AdminScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/schedules")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminScheduleController {

    private final AdminScheduleService adminScheduleService;

    @PostMapping("/generate")
    public ApiResponse<String> generateSchedules(@RequestBody ScheduleGenerateRequest request) {
        int count = adminScheduleService.generateSchedules(request);
        return ApiResponse.success("Đã tạo thành công " + count + " ca khám!");
    }
}