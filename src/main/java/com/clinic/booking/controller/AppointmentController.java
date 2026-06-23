package com.clinic.booking.controller;

import com.clinic.booking.dto.AppointmentDTO;
import com.clinic.booking.service.AppointmentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentDTO request, HttpServletRequest httpRequest) {
        try {
            // VNPAY cần IP Address của client để tạo giao dịch
            String ipAddress = httpRequest.getHeader("X-Forwarded-For");
            if (ipAddress == null || ipAddress.isEmpty()) {
                ipAddress = httpRequest.getRemoteAddr();
            }

            // Chuyển IP vào service
            AppointmentDTO createdAppointment = appointmentService.createAppointment(request, ipAddress);
            return ResponseEntity.ok(createdAppointment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}