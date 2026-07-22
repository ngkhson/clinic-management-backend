package com.clinic.booking.controller;

import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.dto.staff.*;
import com.clinic.booking.service.AdminStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/admin/staff")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminStaffController {

    private final AdminStaffService adminStaffService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_SYSTEM')")
    public ApiResponse<List<StaffResponse>> getAllStaffs() {
        return ApiResponse.success(adminStaffService.getAllStaffs());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_SYSTEM')")
    public ApiResponse<StaffResponse> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        return ApiResponse.success(adminStaffService.createStaff(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_SYSTEM')")
    public ApiResponse<StaffResponse> updateStaff(@PathVariable Long id, @Valid @RequestBody UpdateStaffRequest request) {
        return ApiResponse.success(adminStaffService.updateStaff(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_SYSTEM')")
    public ApiResponse<Object> toggleStatus(@PathVariable Long id) {
        adminStaffService.toggleStaffStatus(id);
        return ApiResponse.success("Đổi trạng thái thành công!");
    }
}
