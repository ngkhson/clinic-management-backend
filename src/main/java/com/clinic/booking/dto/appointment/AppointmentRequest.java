package com.clinic.booking.dto.appointment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    // Các thông tin Front-end gửi lên để tạo lịch khám
    private Long doctorId;
    private Long scheduleId;
    private LocalDate appointmentDate;
    private String symptoms;
    private String paymentType; // 'PAY_NOW' hoặc 'PAY_LATER'
}