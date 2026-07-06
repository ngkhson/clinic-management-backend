package com.clinic.booking.controller;

import com.clinic.booking.dto.specialty.SpecialtyRequest;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.dto.specialty.SpecialtyResponse;
import com.clinic.booking.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/specialties")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminSpecialtyController {

    private final SpecialtyService specialtyService;

    // Đổi kiểu trả về thành SpecialtyDTO
    @PostMapping
    public ApiResponse<SpecialtyResponse> createSpecialty(@RequestBody SpecialtyRequest request) {
        return ApiResponse.success(specialtyService.createSpecialty(request));
    }

    // Đổi kiểu trả về thành SpecialtyDTO
    @PutMapping("/{id}")
    public ApiResponse<SpecialtyResponse> updateSpecialty(@PathVariable Long id, @RequestBody SpecialtyRequest request) {
        return ApiResponse.success(specialtyService.updateSpecialty(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteSpecialty(@PathVariable Long id) {
        specialtyService.deleteSpecialty(id);
        return ApiResponse.success("Xóa thành công!");
    }
}