package com.clinic.booking.controller;

import com.clinic.booking.dto.appointment.AppointmentRequest;
import org.springframework.http.ResponseEntity;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.dto.appointment.AppointmentResponse;
import com.clinic.booking.service.AppointmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ApiResponse<Object> createAppointment(@RequestBody AppointmentRequest request) {
        try {
            AppointmentResponse createdAppointment = appointmentService.createAppointment(request);
            return ApiResponse.success(createdAppointment);
        } catch (RuntimeException e) {
            return ApiResponse.builder().code(400).message(e.getMessage()).build();
        }
    }
}