package com.clinic.booking.controller;

import com.clinic.booking.dto.AppointmentDTO;
import com.clinic.booking.dto.MedicalRecordDTO;
import com.clinic.booking.service.DoctorPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DoctorPortalController {

    private final DoctorPortalService doctorPortalService;

    // 1. Lấy danh sách lịch hẹn của Bác sĩ (có thể truyền tham số ?status=PENDING)
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentDTO>> getAppointments(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(doctorPortalService.getDoctorAppointments(status));
    }

    // 2. Cập nhật trạng thái lịch hẹn
    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        doctorPortalService.updateAppointmentStatus(id, status);
        return ResponseEntity.ok("Cập nhật trạng thái thành công!");
    }

    // 3. Tạo Hồ sơ bệnh án
    @PostMapping("/medical-records")
    public ResponseEntity<MedicalRecordDTO> createMedicalRecord(@RequestBody MedicalRecordDTO request) {
        return ResponseEntity.ok(doctorPortalService.createMedicalRecord(request));
    }
}