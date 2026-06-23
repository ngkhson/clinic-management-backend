package com.clinic.booking.controller;

import com.clinic.booking.dto.AdminReceptionRequest;
import com.clinic.booking.service.AdminReceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reception")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminReceptionController {

    private final AdminReceptionService receptionService;

    @PostMapping
    public ResponseEntity<?> createReception(@RequestBody AdminReceptionRequest request) {
        try {
            return ResponseEntity.ok(receptionService.createReceptionAndVitals(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}