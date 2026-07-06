package com.clinic.booking.controller;

import com.clinic.booking.dto.doctor.DoctorResponse;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    // THÊM API NÀY: Lấy tất cả bác sĩ
    @GetMapping
    public ApiResponse<List<DoctorResponse>> getAllDoctors() {
        return ApiResponse.success(doctorService.getAllDoctors());
    }

    // API: http://localhost:8080/api/doctors/specialty/1
    @GetMapping("/specialty/{specialtyId}")
    public ApiResponse<List<DoctorResponse>> getDoctorsBySpecialty(@PathVariable Long specialtyId) {
        return ApiResponse.success(doctorService.getDoctorsBySpecialtyId(specialtyId));
    }

    // API: http://localhost:8080/api/doctors/1
    @GetMapping("/{id}")
    public ApiResponse<DoctorResponse> getDoctorById(@PathVariable Long id) {
        return ApiResponse.success(doctorService.getDoctorById(id));
    }
}