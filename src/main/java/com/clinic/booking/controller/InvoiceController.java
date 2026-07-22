package com.clinic.booking.controller;

import com.clinic.booking.dto.invoice.InvoiceDetailResponse;
import com.clinic.booking.dto.invoice.InvoiceResponse;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_BILLING')")
public class InvoiceController {

    private final InvoiceService invoiceService;

    // Lấy danh sách tất cả hóa đơn (Cho thu ngân)
    @GetMapping
    public ApiResponse<List<InvoiceResponse>> getAllInvoices() {
        return ApiResponse.success(invoiceService.getAllInvoices());
    }

    // Lịch sử thu ngân có phân trang
    @GetMapping("/history")
    public ApiResponse<Page<InvoiceResponse>> getInvoiceHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String method) {
        return ApiResponse.success(invoiceService.getInvoiceHistory(page, size, search, type, method));
    }

    // Tự động tính toán và tạo Hóa đơn từ ID Lịch hẹn
    @PostMapping("/generate/{appointmentId}")
    public ApiResponse<InvoiceResponse> generateInvoice(@PathVariable Long appointmentId) {
        return ApiResponse.success(invoiceService.generateInvoice(appointmentId));
    }

    // Lấy hóa đơn theo ID Lịch hẹn
    @GetMapping("/appointment/{appointmentId}")
    public ApiResponse<InvoiceResponse> getInvoiceByAppointmentId(@PathVariable Long appointmentId) {
        return ApiResponse.success(invoiceService.getInvoiceByAppointmentId(appointmentId));
    }

    // Lấy chi tiết dịch vụ & thuốc của hóa đơn
    @GetMapping("/{id}/details")
    public ApiResponse<InvoiceDetailResponse> getInvoiceDetails(@PathVariable Long id) {
        return ApiResponse.success(invoiceService.getInvoiceDetails(id));
    }

    // Xác nhận thu tiền
    @PutMapping("/{id}/pay")
    public ApiResponse<Object> payInvoice(
            @PathVariable Long id,
            @RequestParam String method) {
        return ApiResponse.success(invoiceService.payInvoice(id, method));
    }
}