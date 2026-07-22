package com.clinic.booking.controller;

import com.clinic.booking.entity.MedicalService;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.MedicalServiceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/page")
    public ApiResponse<Page<MedicalService>> getServicesPage(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(medicalServiceService.getServicesPage(search, page, size));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_SYSTEM')")
    public ApiResponse<?> create(@RequestBody Object requestObj) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (requestObj instanceof List) {
            List<MedicalService> services = objectMapper.convertValue(requestObj, new TypeReference<List<MedicalService>>() {});
            return ApiResponse.success(medicalServiceService.createServices(services));
        } else {
            MedicalService service = objectMapper.convertValue(requestObj, MedicalService.class);
            return ApiResponse.success(medicalServiceService.createService(service));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_SYSTEM')")
    public ApiResponse<MedicalService> update(@PathVariable Long id, @RequestBody MedicalService service) {
        return ApiResponse.success(medicalServiceService.updateService(id, service));
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_SYSTEM')")
    public ApiResponse<Void> toggleStatus(@PathVariable Long id) {
        medicalServiceService.toggleStatus(id);
        return ApiResponse.success();
    }
}