package com.clinic.booking.controller;

import com.clinic.booking.dto.pharmacy.RetailMedicineRequest;
import com.clinic.booking.entity.Invoice;
import org.springframework.http.ResponseEntity;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.RetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/retail")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RetailController {

    private final RetailService retailService;

    @GetMapping
    public ApiResponse<Object> getAllRetailInvoices() {
        return ApiResponse.success(retailService.getAllRetailInvoices());
    }

    @PostMapping
    public ApiResponse<Object> createRetailInvoice(@Valid @RequestBody RetailMedicineRequest request) {
        try {
            Invoice invoice = retailService.createRetailInvoice(request);
            return ApiResponse.success(invoice);
        } catch (RuntimeException e) {
            // Sẽ trả về lỗi nếu không đủ số lượng thuốc
            return ApiResponse.builder().code(400).message(e.getMessage()).build();
        }
    }
}