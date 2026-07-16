package com.clinic.booking.controller;

import com.clinic.booking.exception.AppException;
import org.springframework.http.ResponseEntity;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.entity.Invoice;
import com.clinic.booking.repository.InvoiceRepository;
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
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;

    @PostMapping("/create-url")
    public ApiResponse<String> createPaymentUrl(@RequestBody PaymentUrlRequest request, HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getRemoteAddr();
        double amount = 0;

        if ("INVOICE".equalsIgnoreCase(request.getTargetType()) || "RETAIL".equalsIgnoreCase(request.getTargetType())) {
            Invoice invoice = invoiceRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND)); 
            amount = invoice.getTotalAmount();
        } else {
            return ApiResponse.<String>builder().code(400).message("Loại thanh toán không hợp lệ.").build();
        }

        String paymentUrl = paymentService.createPaymentUrl(request.getTargetType(), request.getTargetId(), amount, ipAddress);
        return ApiResponse.success(paymentUrl);
    }

    @GetMapping("/vnpay-return")
    public ApiResponse<Object> handleVNPayReturn(@RequestParam Map<String, String> queryParams) {
        String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
        String vnp_TxnRef = queryParams.get("vnp_TxnRef");

        if (vnp_TxnRef != null && vnp_ResponseCode != null) {
            if ("00".equals(vnp_ResponseCode)) {
                try {
                    String[] parts = vnp_TxnRef.split("-");
                    String type = parts[0];
                    Long targetId = Long.parseLong(parts[1]);

                    if ("INV".equals(type) || "RET".equals(type)) {
                        invoiceService.payInvoice(targetId, "TRANSFER");
                        return ApiResponse.success("Thanh toán thành công.");
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