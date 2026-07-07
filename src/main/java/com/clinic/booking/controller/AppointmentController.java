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

    @PutMapping("/{id}")
    public ApiResponse<Object> updateAppointment(@PathVariable Long id, @RequestBody com.clinic.booking.dto.appointment.AppointmentUpdateRequest request) {
        try {
            AppointmentResponse updatedAppointment = appointmentService.updateAppointment(id, request);
            return ApiResponse.success(updatedAppointment);
        } catch (RuntimeException e) {
            return ApiResponse.builder().code(400).message(e.getMessage()).build();
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> cancelAppointment(@PathVariable Long id) {
        try {
            appointmentService.cancelAppointment(id);
            return ApiResponse.success("Huỷ lịch hẹn thành công");
        } catch (RuntimeException e) {
            return ApiResponse.<String>builder().code(400).message(e.getMessage()).build();
        }
    }
}