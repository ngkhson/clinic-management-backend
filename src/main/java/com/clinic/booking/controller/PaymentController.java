package com.clinic.booking.controller;

import com.clinic.booking.exception.AppException;
import org.springframework.http.ResponseEntity;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.entity.Invoice;
import com.clinic.booking.entity.RetailInvoice;
import com.clinic.booking.repository.InvoiceRepository;
import com.clinic.booking.repository.RetailInvoiceRepository;
import com.clinic.booking.service.InvoiceService;
import com.clinic.booking.service.PaymentService;
import com.clinic.booking.service.RetailService;
import com.clinic.booking.dto.payment.PaymentUrlRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PaymentController {

    private final InvoiceRepository invoiceRepository;
    private final RetailInvoiceRepository retailInvoiceRepository;
    private final InvoiceService invoiceService;
    private final RetailService retailService;
    private final PaymentService paymentService;

    @PostMapping("/create-url")
    public ApiResponse<String> createPaymentUrl(@RequestBody PaymentUrlRequest request, HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getRemoteAddr();
        double amount = 0;

        if ("INVOICE".equalsIgnoreCase(request.getTargetType())) {
            Invoice invoice = invoiceRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND)); // Or custom error code
            amount = invoice.getTotalAmount();
        } else if ("RETAIL".equalsIgnoreCase(request.getTargetType())) {
            RetailInvoice retailInvoice = retailInvoiceRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Hóa đơn bán lẻ"));
            amount = retailInvoice.getTotalAmount();
        } else {
            return ApiResponse.<String>builder().code(400).message("Loại thanh toán không hợp lệ.").build();
        }

        String paymentUrl = paymentService.createPaymentUrl(request.getTargetType(), request.getTargetId(), amount, ipAddress);
        return ApiResponse.success(paymentUrl);
    }

    @GetMapping("/vnpay-return")
    public ApiResponse<Object> handleVNPayReturn(@RequestParam Map<String, String> queryParams) {
        String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
        String vnp_TxnRef = queryParams.get("vnp_TxnRef"); // Format: prefix-targetId-random

        if (vnp_TxnRef != null && vnp_ResponseCode != null) {
            // Mã "00" của VNPAY nghĩa là giao dịch thành công
            if ("00".equals(vnp_ResponseCode)) {
                try {
                    String[] parts = vnp_TxnRef.split("-");
                    String type = parts[0];
                    Long targetId = Long.parseLong(parts[1]);

                    if ("INV".equals(type)) {
                        invoiceService.payInvoice(targetId, "VNPAY");
                        return ApiResponse.success("Thanh toán Hóa đơn khám bệnh thành công.");
                    } else if ("RET".equals(type)) {
                        retailService.payRetailInvoice(targetId, "VNPAY");
                        return ApiResponse.success("Thanh toán Hóa đơn bán lẻ thành công.");
                    } else {
                        return ApiResponse.builder().code(400).message("Mã giao dịch không hợp lệ.").build();
                    }
                } catch (Exception e) {
                    return ApiResponse.builder().code(400).message("Lỗi cập nhật trạng thái thanh toán: " + e.getMessage()).build();
                }
            } else {
                return ApiResponse.builder().code(400).message("Giao dịch thanh toán thất bại hoặc bị hủy bởi người dùng.").build();
            }
        }
        return ApiResponse.builder().code(400).message("Dữ liệu trả về từ VNPAY không hợp lệ.").build();
    }
}