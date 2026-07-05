package com.clinic.booking.controller;

import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.entity.Appointment;
import com.clinic.booking.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PaymentController {

    private final AppointmentRepository appointmentRepository;

    @GetMapping("/vnpay-return")
    public ResponseEntity<?> handleVNPayReturn(@RequestParam Map<String, String> queryParams) {
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

                    return ResponseEntity.ok("Thanh toán thành công và đã cập nhật trạng thái lịch hẹn.");
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body("Lỗi cập nhật CSDL: " + e.getMessage());
                }
            } else {
                return ResponseEntity.badRequest().body("Giao dịch thanh toán thất bại hoặc bị hủy bởi người dùng.");
            }
        }
        return ResponseEntity.badRequest().body("Dữ liệu trả về từ VNPAY không hợp lệ.");
    }
}