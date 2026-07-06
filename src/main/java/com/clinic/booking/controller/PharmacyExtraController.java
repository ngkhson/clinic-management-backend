package com.clinic.booking.controller;

import com.clinic.booking.dto.pharmacy.PharmacyReportResponse;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.PharmacyExtraService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/medicines/extra")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PharmacyExtraController {

    private final PharmacyExtraService pharmacyExtraService;

    // Lấy số liệu báo cáo
    @GetMapping("/report")
    public ApiResponse<PharmacyReportResponse> getReport() {
        return ApiResponse.success(pharmacyExtraService.getReportSummary());
    }

    // Lấy nội dung ghi chú
    @GetMapping("/note")
    public ApiResponse<Map<String, String>> getNote() {
        return ApiResponse.success(Map.of("content", pharmacyExtraService.getNote()));
    }

    // Lưu nội dung ghi chú
    @PostMapping("/note")
    public ApiResponse<String> saveNote(@RequestBody Map<String, String> request) {
        pharmacyExtraService.saveNote(request.get("content"));
        return ApiResponse.success("Đã lưu ghi chú!");
    }
}