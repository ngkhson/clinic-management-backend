package com.clinic.booking.controller;

import com.clinic.booking.dto.invoice.InvoiceResponse;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    // Lấy danh sách tất cả hóa đơn (Cho thu ngân)
    @GetMapping
    public ApiResponse<List<InvoiceResponse>> getAllInvoices() {
        return ApiResponse.success(invoiceService.getAllInvoices());
    }

    // Tự động tính toán và tạo Hóa đơn từ ID Lịch hẹn
    @PostMapping("/generate/{appointmentId}")
    public ApiResponse<InvoiceResponse> generateInvoice(@PathVariable Long appointmentId) {
        return ApiResponse.success(invoiceService.generateInvoice(appointmentId));
    }

    // Xác nhận thu tiền
    @PutMapping("/{id}/pay")
    public ApiResponse<Object> payInvoice(
            @PathVariable Long id,
            @RequestParam String method) {
        return ApiResponse.success(invoiceService.payInvoice(id, method));
    }
}