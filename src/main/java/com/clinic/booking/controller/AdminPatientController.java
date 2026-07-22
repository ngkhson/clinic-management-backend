package com.clinic.booking.controller;

import com.clinic.booking.dto.user.UserResponse;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.AdminPatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/patients")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminPatientController {

    private final AdminPatientService adminPatientService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('VIEW_PATIENT_LIST')")
    public ApiResponse<Page<UserResponse>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ApiResponse.success(adminPatientService.getAllPatients(page, size, search));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('VIEW_PATIENT_LIST')")
    public ApiResponse<List<UserResponse>> getPatients() {
        return ApiResponse.success(adminPatientService.getPatients());
    }

    @PutMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_SYSTEM')")
    public ApiResponse<String> toggleStatus(@PathVariable Long id) {
        adminPatientService.togglePatientStatus(id);
        return ApiResponse.success("Cập nhật trạng thái thành công!");
    }
}