package com.clinic.booking.controller;

import com.clinic.booking.dto.appointment.AppointmentResponse;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getDashboardStats() {
        return ApiResponse.success(adminDashboardService.getDashboardStats());
    }

    @GetMapping("/all-appointments")
    public ApiResponse<List<AppointmentResponse>> getAllAppointments() {
        return ApiResponse.success(adminDashboardService.getAllAppointments());
    }

    // THÊM API NÀY
    @PutMapping("/appointments/{id}/status")
    public ApiResponse<String> updateAppointmentStatus(@PathVariable Long id, @RequestParam String status) {
        adminDashboardService.updateAppointmentStatus(id, status);
        return ApiResponse.success("Cập nhật trạng thái thành công");
    }
}