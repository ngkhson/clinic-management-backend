package com.clinic.booking.controller;

import com.clinic.booking.dto.PharmacyReportDTO;
import com.clinic.booking.service.PharmacyExtraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PharmacyReportDTO> getReport() {
        return ResponseEntity.ok(pharmacyExtraService.getReportSummary());
    }

    // Lấy nội dung ghi chú
    @GetMapping("/note")
    public ResponseEntity<Map<String, String>> getNote() {
        return ResponseEntity.ok(Map.of("content", pharmacyExtraService.getNote()));
    }

    // Lưu nội dung ghi chú
    @PostMapping("/note")
    public ResponseEntity<String> saveNote(@RequestBody Map<String, String> request) {
        pharmacyExtraService.saveNote(request.get("content"));
        return ResponseEntity.ok("Đã lưu ghi chú!");
    }
}