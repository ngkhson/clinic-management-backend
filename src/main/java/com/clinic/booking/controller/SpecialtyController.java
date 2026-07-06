package com.clinic.booking.controller;

import com.clinic.booking.dto.specialty.SpecialtyResponse;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
@CrossOrigin(origins = "*") // Tạm thời cho phép mọi domain gọi API để test với React
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @GetMapping
    public ApiResponse<List<SpecialtyResponse>> getAll() {
        return ApiResponse.success(specialtyService.getAllSpecialties());
    }

//    @PostMapping
//    public ApiResponse<SpecialtyDTO> create(@RequestBody SpecialtyDTO dto) {
//        return ApiResponse.success(specialtyService.createSpecialty(dto));
//    }
}