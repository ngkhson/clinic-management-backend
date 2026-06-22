package com.clinic.booking.controller;

import com.clinic.booking.entity.MedicalService;
import com.clinic.booking.service.MedicalServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MedicalServiceController {

    private final MedicalServiceService medicalServiceService;

    @GetMapping
    public ResponseEntity<List<MedicalService>> getAll() {
        return ResponseEntity.ok(medicalServiceService.getAllActiveServices());
    }

    @PostMapping
    public ResponseEntity<MedicalService> create(@RequestBody MedicalService service) {
        return ResponseEntity.ok(medicalServiceService.createService(service));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalService> update(@PathVariable Long id, @RequestBody MedicalService service) {
        return ResponseEntity.ok(medicalServiceService.updateService(id, service));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Void> toggleStatus(@PathVariable Long id) {
        medicalServiceService.toggleStatus(id);
        return ResponseEntity.ok().build();
    }
}