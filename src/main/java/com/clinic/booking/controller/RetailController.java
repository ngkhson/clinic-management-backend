package com.clinic.booking.controller;

import com.clinic.booking.dto.RetailRequestDTO;
import com.clinic.booking.service.RetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/retail")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RetailController {

    private final RetailService retailService;

    @PostMapping
    public ResponseEntity<?> createRetailInvoice(@RequestBody RetailRequestDTO request) {
        try {
            retailService.createRetailInvoice(request);
            return ResponseEntity.ok("Thanh toán thành công!");
        } catch (RuntimeException e) {
            // Sẽ trả về lỗi nếu không đủ số lượng thuốc
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}