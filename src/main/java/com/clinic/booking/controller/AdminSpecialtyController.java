package com.clinic.booking.controller;

import com.clinic.booking.dto.specialty.SpecialtyRequest;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.dto.specialty.SpecialtyResponse;
import com.clinic.booking.service.SpecialtyService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/specialties")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminSpecialtyController {

    private final SpecialtyService specialtyService;

    @PostMapping
    public ApiResponse<?> createSpecialty(@RequestBody Object requestObj) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (requestObj instanceof java.util.List) {
            java.util.List<SpecialtyRequest> requests = objectMapper.convertValue(requestObj, new TypeReference<List<SpecialtyRequest>>() {});
            return ApiResponse.success(specialtyService.createSpecialties(requests));
        } else {
            SpecialtyRequest request = objectMapper.convertValue(requestObj, SpecialtyRequest.class);
            return ApiResponse.success(specialtyService.createSpecialty(request));
        }
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