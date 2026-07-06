package com.clinic.booking.controller;

import com.clinic.booking.exception.AppException;
import org.springframework.http.ResponseEntity;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.entity.Appointment;
import com.clinic.booking.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PaymentController {

    private final AppointmentRepository appointmentRepository;

    @GetMapping("/vnpay-return")
    public ApiResponse<Object> handleVNPayReturn(@RequestParam Map<String, String> queryParams) {
        String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
        String vnp_TxnRef = queryParams.get("vnp_TxnRef"); // Định dạng mà ta đã tạo: randomString-appointmentId

        if (vnp_TxnRef != null && vnp_ResponseCode != null) {
            // Mã "00" của VNPAY nghĩa là giao dịch thành công
            if ("00".equals(vnp_ResponseCode)) {
                try {
                    // Tách chuỗi để lấy ID lịch hẹn ở phía sau dấu "-"
                    String[] parts = vnp_TxnRef.split("-");
                    Long appointmentId = Long.parseLong(parts[1]);

                    Appointment appointment = appointmentRepository.findById(appointmentId)
                            .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

                    // Khách đã trả tiền online -> Cập nhật trạng thái Lịch hẹn thành Đã Xác Nhận
                    appointment.setStatus("CONFIRMED");
                    appointmentRepository.save(appointment);

                    return ApiResponse.success("Thanh toán thành công và đã cập nhật trạng thái lịch hẹn.");
                } catch (Exception e) {
                    return ApiResponse.builder().code(400).message("Lỗi cập nhật CSDL: " + e.getMessage()).build();
                }
            } else {
                return ApiResponse.builder().code(400).message("Giao dịch thanh toán thất bại hoặc bị hủy bởi người dùng.").build();
            }
        }
        return ApiResponse.builder().code(400).message("Dữ liệu trả về từ VNPAY không hợp lệ.").build();
    }
}