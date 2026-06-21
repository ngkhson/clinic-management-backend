package com.clinic.booking.controller;

import com.clinic.booking.dto.UserDTO;
import com.clinic.booking.service.AdminPatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/patients")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminPatientController {

    private final AdminPatientService adminPatientService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllPatients() {
        return ResponseEntity.ok(adminPatientService.getAllPatients());
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<String> toggleStatus(@PathVariable Long id) {
        adminPatientService.togglePatientStatus(id);
        return ResponseEntity.ok("Cập nhật trạng thái thành công!");
    }
}