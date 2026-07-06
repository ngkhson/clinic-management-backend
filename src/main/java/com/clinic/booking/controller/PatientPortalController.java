package com.clinic.booking.controller;

import com.clinic.booking.dto.appointment.AppointmentResponse;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.dto.record.MedicalRecordResponse;
import com.clinic.booking.service.PatientPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PatientPortalController {

    private final PatientPortalService patientPortalService;

    @GetMapping("/appointments")
    public ApiResponse<List<AppointmentResponse>> getMyAppointments() {
        return ApiResponse.success(patientPortalService.getMyAppointments());
    }

    @GetMapping("/appointments/{id}/record")
    public ApiResponse<MedicalRecordResponse> getMedicalRecord(@PathVariable Long id) {
        return ApiResponse.success(patientPortalService.getMedicalRecord(id));
    }
}