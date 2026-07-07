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
    private Long specialtyId;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String symptoms;
}