package com.clinic.booking.controller;

import com.clinic.booking.dto.doctor.DoctorCreationRequest;
import com.clinic.booking.dto.doctor.DoctorResponse;
import com.clinic.booking.service.AdminDoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/doctors")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminDoctorController {

    private final AdminDoctorService adminDoctorService;

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        return ResponseEntity.ok(adminDoctorService.getAllDoctors());
    }

    @PostMapping
    public ResponseEntity<?> createDoctor(@RequestBody DoctorCreationRequest request) {
        try {
            DoctorResponse newDoctor = adminDoctorService.createDoctor(request);
            return ResponseEntity.ok(newDoctor);
        } catch (RuntimeException e) {
            // Trả về lỗi 400 kèm câu thông báo (VD: Email đã tồn tại)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDoctor(@PathVariable Long id, @RequestBody DoctorCreationRequest request) {
        return ResponseEntity.ok(adminDoctorService.updateDoctor(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
        adminDoctorService.deleteDoctor(id);
        return ResponseEntity.ok("Xóa thành công!");
    }
}