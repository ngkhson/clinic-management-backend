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

    @PostMapping
    public ApiResponse<?> createSpecialty(@RequestBody com.fasterxml.jackson.databind.JsonNode requestNode) {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        if (requestNode.isArray()) {
            java.util.List<SpecialtyRequest> requests = objectMapper.convertValue(requestNode, new com.fasterxml.jackson.core.type.TypeReference<java.util.List<SpecialtyRequest>>() {});
            return ApiResponse.success(specialtyService.createSpecialties(requests));
        } else {
            SpecialtyRequest request = objectMapper.convertValue(requestNode, SpecialtyRequest.class);
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