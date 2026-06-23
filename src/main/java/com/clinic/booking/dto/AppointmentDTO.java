package com.clinic.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private Long id;
    private Long doctorId;
    private Long scheduleId;
    private LocalDate appointmentDate;
    private String symptoms;

    // --- THÊM 2 TRƯỜNG MỚI ĐỂ NHẬN LOẠI THANH TOÁN VÀ TRẢ VỀ URL ---
    private String paymentType; // 'PAY_NOW' hoặc 'PAY_LATER'
    private String paymentUrl;  // Chứa URL của VNPAY nếu PAY_NOW

    // Các trường dưới đây dùng để trả về thông tin (Response) cho Front-end xem
    private String doctorName;
    private String patientName;
    private String timeSlot;
    private String status;
    private LocalDateTime createdAt;
}