package com.clinic.booking.controller;

import com.clinic.booking.dto.doctor.DoctorCreationRequest;
import org.springframework.http.ResponseEntity;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.dto.doctor.AdminDoctorResponse;
import com.clinic.booking.service.AdminDoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/doctors")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminDoctorController {

    private final AdminDoctorService adminDoctorService;

    @GetMapping
    public ApiResponse<List<AdminDoctorResponse>> getAllDoctors() {
        return ApiResponse.success(adminDoctorService.getAllDoctors());
    }

    @PostMapping
    public ApiResponse<Object> createDoctor(@RequestBody DoctorCreationRequest request) {
        try {
            AdminDoctorResponse newDoctor = adminDoctorService.createDoctor(request);
            return ApiResponse.success(newDoctor);
        } catch (RuntimeException e) {
            // Trả về lỗi 400 kèm câu thông báo (VD: Email đã tồn tại)
            return ApiResponse.builder().code(400).message(e.getMessage()).build();
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Object> updateDoctor(@PathVariable Long id, @RequestBody DoctorCreationRequest request) {
        return ApiResponse.success(adminDoctorService.updateDoctor(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Object> deleteDoctor(@PathVariable Long id) {
        adminDoctorService.deleteDoctor(id);
        return ApiResponse.success("Xóa thành công!");
    }
}