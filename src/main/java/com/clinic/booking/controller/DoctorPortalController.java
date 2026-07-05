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

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentDTO>> getAppointments(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(doctorPortalService.getDoctorAppointments(status));
    }

    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        doctorPortalService.updateAppointmentStatus(id, status);
        return ResponseEntity.ok("Cập nhật trạng thái thành công!");
    }

    // THÊM: API Lấy bệnh án nháp nếu Bác sĩ mở lại ca khám
    @GetMapping("/medical-records/appointment/{appointmentId}")
    public ResponseEntity<MedicalRecordDTO> getDraftRecord(@PathVariable Long appointmentId) {
        MedicalRecordDTO record = doctorPortalService.getDraftRecord(appointmentId);
        if (record == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(record);
    }

    // API Lưu Bệnh Án (Xử lý cả Lưu nháp và Hoàn tất dựa vào cờ isDraft)
    @PostMapping("/medical-records")
    public ResponseEntity<?> saveMedicalRecord(@RequestBody MedicalRecordDTO request) {
        try {
            return ResponseEntity.ok(doctorPortalService.saveMedicalRecord(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}