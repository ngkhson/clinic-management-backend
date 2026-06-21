package com.clinic.booking.controller;

import com.clinic.booking.dto.ScheduleDTO;
import com.clinic.booking.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // API: GET http://localhost:8080/api/schedules/doctor/1?date=2026-06-18
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ScheduleDTO>> getDoctorSchedules(
            @PathVariable Long doctorId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(scheduleService.getSchedulesByDoctorAndDate(doctorId, date));
    }
}