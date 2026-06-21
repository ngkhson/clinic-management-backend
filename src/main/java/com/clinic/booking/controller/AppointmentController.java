package com.clinic.booking.controller;

import com.clinic.booking.dto.AppointmentDTO;
import com.clinic.booking.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // API: POST http://localhost:8080/api/appointments
    // Bắt buộc phải có Authorization Header chứa chuỗi Bearer Token
    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentDTO request) {
        try {
            AppointmentDTO createdAppointment = appointmentService.createAppointment(request);
            return ResponseEntity.ok(createdAppointment);
        } catch (RuntimeException e) {
            // Bắt lỗi logic (VD: Ca khám đã đầy) và trả về status 400 Bad Request cho Front-end
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}