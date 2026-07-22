package com.clinic.booking.controller;

import com.clinic.booking.dto.appointment.AppointmentResponse;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_SYSTEM')")
    public ApiResponse<Map<String, Object>> getDashboardStats() {
        return ApiResponse.success(adminDashboardService.getDashboardStats());
    }

    @GetMapping("/all-appointments")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_APPOINTMENT')")
    public ApiResponse<Page<AppointmentResponse>> getAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        return ApiResponse.success(adminDashboardService.getAppointments(page, size, search, status));
    }

    @GetMapping("/all-appointments/all")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_APPOINTMENT')")
    public ApiResponse<List<AppointmentResponse>> getAllAppointmentsList() {
        return ApiResponse.success(adminDashboardService.getAllAppointmentsList());
    }

    // THÊM API NÀY
    @PutMapping("/appointments/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_APPOINTMENT')")
    public ApiResponse<String> updateAppointmentStatus(@PathVariable Long id, @RequestParam String status) {
        adminDashboardService.updateAppointmentStatus(id, status);
        return ApiResponse.success("Cập nhật trạng thái thành công");
    }
}