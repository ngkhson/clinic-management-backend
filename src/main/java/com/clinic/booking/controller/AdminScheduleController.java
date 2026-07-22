package com.clinic.booking.controller;

import com.clinic.booking.dto.schedule.ScheduleUpdateRequest;
import com.clinic.booking.exception.AppException;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.dto.schedule.ScheduleGenerateRequest;
import com.clinic.booking.service.AdminScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/schedules")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_SYSTEM')")
public class AdminScheduleController {

    private final AdminScheduleService adminScheduleService;

    @PostMapping("/generate")
    public ApiResponse<String> generateSchedules(@Valid @RequestBody ScheduleGenerateRequest request) {
        int count = adminScheduleService.generateSchedules(request);
        return ApiResponse.success("Đã tạo thành công " + count + " ca khám!");
    }

    @PutMapping("/{id}")
    public ApiResponse<String> updateSchedule(@PathVariable Long id, @Valid @RequestBody ScheduleUpdateRequest request) {
        adminScheduleService.updateSchedule(id, request);
        return ApiResponse.success("Cập nhật ca khám thành công");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteSchedule(@PathVariable Long id) {
        adminScheduleService.deleteSchedule(id);
        return ApiResponse.success("Xoá ca khám thành công");
    }
}