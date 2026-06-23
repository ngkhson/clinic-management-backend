package com.clinic.booking.controller;

import com.clinic.booking.dto.ImportRequestDTO;
import com.clinic.booking.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/imports")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @PostMapping
    public ResponseEntity<String> createImport(@RequestBody ImportRequestDTO request) {
        try {
            importService.createImportInvoice(request);
            return ResponseEntity.ok("Nhập kho thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}