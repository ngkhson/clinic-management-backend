package com.clinic.booking.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Lỗi hệ thống chung
    UNCATEGORIZED_EXCEPTION(500, "Lỗi hệ thống không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(400, "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),
    
    // Lỗi liên quan đến User và Xác thực (Auth)
    USER_NOT_FOUND(404, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    USER_EXISTED(400, "Email hoặc số điện thoại đã tồn tại", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(401, "Bạn chưa đăng nhập hoặc phiên làm việc đã hết hạn", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(403, "Bạn không có quyền truy cập chức năng này", HttpStatus.FORBIDDEN),
    INVALID_PASSWORD(400, "Mật khẩu không chính xác", HttpStatus.BAD_REQUEST),
    
    // Lỗi nghiệp vụ phòng khám (Doctor, Appointment, Specialty, Pharmacy)
    DOCTOR_NOT_FOUND(404, "Không tìm thấy bác sĩ", HttpStatus.NOT_FOUND),
    APPOINTMENT_NOT_FOUND(404, "Không tìm thấy lịch hẹn", HttpStatus.NOT_FOUND),
    SPECIALTY_NOT_FOUND(404, "Không tìm thấy chuyên khoa", HttpStatus.NOT_FOUND),
    SCHEDULE_NOT_FOUND(404, "Không tìm thấy lịch làm việc", HttpStatus.NOT_FOUND),
    SCHEDULE_FULL(400, "Không còn lịch trống trong khung giờ này", HttpStatus.BAD_REQUEST),
    REVIEW_ALREADY_EXISTS(400, "Bạn đã gửi đánh giá cho ca khám này rồi", HttpStatus.BAD_REQUEST),
    MEDICINE_NOT_FOUND(404, "Không tìm thấy thuốc", HttpStatus.NOT_FOUND),
    OUT_OF_STOCK(400, "Số lượng thuốc trong kho không đủ", HttpStatus.BAD_REQUEST),
    INVALID_ACTION(400, "Hành động không hợp lệ", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
