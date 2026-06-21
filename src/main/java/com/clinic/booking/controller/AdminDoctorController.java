package com.clinic.booking.controller;

import com.clinic.booking.dto.DoctorCreationDTO;
import com.clinic.booking.dto.DoctorDTO;
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
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        return ResponseEntity.ok(adminDoctorService.getAllDoctors());
    }

    @PostMapping
    public ResponseEntity<?> createDoctor(@RequestBody DoctorCreationDTO request) {
        try {
            DoctorDTO newDoctor = adminDoctorService.createDoctor(request);
            return ResponseEntity.ok(newDoctor);
        } catch (RuntimeException e) {
            // Trả về lỗi 400 kèm câu thông báo (VD: Email đã tồn tại)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDoctor(@PathVariable Long id, @RequestBody DoctorCreationDTO request) {
        try {
            return ResponseEntity.ok(adminDoctorService.updateDoctor(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
        try {
            adminDoctorService.deleteDoctor(id);
            return ResponseEntity.ok("Xóa thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}