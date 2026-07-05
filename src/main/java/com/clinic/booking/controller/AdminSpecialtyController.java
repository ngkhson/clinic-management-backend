package com.clinic.booking.controller;

import com.clinic.booking.dto.specialty.SpecialtyRequest;
import com.clinic.booking.dto.specialty.SpecialtyResponse;
import com.clinic.booking.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/specialties")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminSpecialtyController {

    private final SpecialtyService specialtyService;

    // Đổi kiểu trả về thành SpecialtyDTO
    @PostMapping
    public ResponseEntity<SpecialtyResponse> createSpecialty(@RequestBody SpecialtyRequest request) {
        return ResponseEntity.ok(specialtyService.createSpecialty(request));
    }

    // Đổi kiểu trả về thành SpecialtyDTO
    @PutMapping("/{id}")
    public ResponseEntity<SpecialtyResponse> updateSpecialty(@PathVariable Long id, @RequestBody SpecialtyRequest request) {
        return ResponseEntity.ok(specialtyService.updateSpecialty(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSpecialty(@PathVariable Long id) {
        specialtyService.deleteSpecialty(id);
        return ResponseEntity.ok("Xóa thành công!");
    }
}