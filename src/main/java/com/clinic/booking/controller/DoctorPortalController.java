package com.clinic.booking.controller;

import com.clinic.booking.dto.appointment.AppointmentResponse;
import org.springframework.http.ResponseEntity;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.dto.record.MedicalRecordRequest;
import com.clinic.booking.dto.record.MedicalRecordResponse;
import com.clinic.booking.service.DoctorPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DoctorPortalController {

    private final DoctorPortalService doctorPortalService;

    @GetMapping("/appointments")
    public ApiResponse<List<AppointmentResponse>> getAppointments(@RequestParam(required = false) String status) {
        return ApiResponse.success(doctorPortalService.getDoctorAppointments(status));
    }

    @PutMapping("/appointments/{id}/status")
    public ApiResponse<String> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        doctorPortalService.updateAppointmentStatus(id, status);
        return ApiResponse.success("Cập nhật trạng thái thành công!");
    }

    // THÊM: API Lấy bệnh án nháp nếu Bác sĩ mở lại ca khám
    @GetMapping("/medical-records/appointment/{appointmentId}")
    public ApiResponse<MedicalRecordResponse> getDraftRecord(@PathVariable Long appointmentId) {
        MedicalRecordResponse record = doctorPortalService.getDraftRecord(appointmentId);
        if (record == null) {
            return ApiResponse.success(null);
        }
        return ApiResponse.success(record);
    }

    // API Lưu Bệnh Án (Xử lý cả Lưu nháp và Hoàn tất dựa vào cờ isDraft)
    @PostMapping("/medical-records")
    public ApiResponse<Object> saveMedicalRecord(@RequestBody MedicalRecordRequest request) {
        return ApiResponse.success(doctorPortalService.saveMedicalRecord(request));
    }
}