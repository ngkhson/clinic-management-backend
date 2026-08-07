package com.clinic.booking.controller;

import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.dto.report.MedicineReportResponse;
import com.clinic.booking.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/medicines")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGE_MEDICINE', 'MANAGE_BILLING')")
    public ApiResponse<List<MedicineReportResponse>> getMedicineReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDate.now().atTime(LocalTime.MAX);

        return ApiResponse.<List<MedicineReportResponse>>builder()
                .code(1000)
                .result(reportService.getMedicineSalesReport(start, end))
                .build();
    }
}
