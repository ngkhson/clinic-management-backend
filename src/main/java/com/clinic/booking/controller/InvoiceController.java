package com.clinic.booking.controller;

import com.clinic.booking.dto.InvoiceDTO;
import com.clinic.booking.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    // Tự động tính toán và tạo Hóa đơn từ ID Lịch hẹn
    @PostMapping("/generate/{appointmentId}")
    public ResponseEntity<InvoiceDTO> generateInvoice(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(invoiceService.generateInvoice(appointmentId));
    }

    // Xác nhận thu tiền
    @PutMapping("/{id}/pay")
    public ResponseEntity<?> payInvoice(
            @PathVariable Long id,
            @RequestParam String method) {
        try {
            return ResponseEntity.ok(invoiceService.payInvoice(id, method));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}