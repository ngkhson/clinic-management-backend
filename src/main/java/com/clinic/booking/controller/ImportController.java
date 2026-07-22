package com.clinic.booking.controller;

import com.clinic.booking.dto.pharmacy.ImportMedicineRequest;
import org.springframework.http.ResponseEntity;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/imports")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_MEDICINE')")
public class ImportController {

    private final ImportService importService;

    @PostMapping
    public ApiResponse<String> createImport(@Valid @RequestBody ImportMedicineRequest request) {
        try {
            importService.createImportInvoice(request);
            return ApiResponse.success("Nhập kho thành công!");
        } catch (Exception e) {
            return ApiResponse.<String>builder().code(400).message(e.getMessage()).build();
        }
    }
}