package com.clinic.booking.controller;

import com.clinic.booking.dto.pharmacy.RetailMedicineRequest;
import org.springframework.http.ResponseEntity;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.RetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/retail")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RetailController {

    private final RetailService retailService;

    @PostMapping
    public ApiResponse<Object> createRetailInvoice(@RequestBody RetailMedicineRequest request) {
        try {
            retailService.createRetailInvoice(request);
            return ApiResponse.success("Thanh toán thành công!");
        } catch (RuntimeException e) {
            // Sẽ trả về lỗi nếu không đủ số lượng thuốc
            return ApiResponse.builder().code(400).message(e.getMessage()).build();
        }
    }
}