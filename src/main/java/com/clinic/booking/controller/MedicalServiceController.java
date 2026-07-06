package com.clinic.booking.controller;

import com.clinic.booking.entity.MedicalService;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.MedicalServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MedicalServiceController {

    private final MedicalServiceService medicalServiceService;

    @GetMapping
    public ApiResponse<List<MedicalService>> getAll() {
        return ApiResponse.success(medicalServiceService.getAllActiveServices());
    }

    // THÊM API NÀY: Dành cho Admin (lấy tất cả)
    @GetMapping("/all")
    public ApiResponse<List<MedicalService>> getAllForAdmin() {
        return ApiResponse.success(medicalServiceService.getAllServices());
    }

    @PostMapping
    public ApiResponse<MedicalService> create(@RequestBody MedicalService service) {
        return ApiResponse.success(medicalServiceService.createService(service));
    }

    @PutMapping("/{id}")
    public ApiResponse<MedicalService> update(@PathVariable Long id, @RequestBody MedicalService service) {
        return ApiResponse.success(medicalServiceService.updateService(id, service));
    }

    @PatchMapping("/{id}/toggle-status")
    public ApiResponse<Void> toggleStatus(@PathVariable Long id) {
        medicalServiceService.toggleStatus(id);
        return ApiResponse.success();
    }
}