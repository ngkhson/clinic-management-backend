package com.clinic.booking.controller;

import com.clinic.booking.dto.DoctorDTO;
import com.clinic.booking.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    // API: http://localhost:8080/api/doctors/specialty/1
    @GetMapping("/specialty/{specialtyId}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialty(@PathVariable Long specialtyId) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialtyId(specialtyId));
    }

    // API: http://localhost:8080/api/doctors/1
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }
}