package com.clinic.booking.dto.appointment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    // Các thông tin Front-end gửi lên để tạo lịch khám
    @NotNull(message = "Vui lòng chọn chuyên khoa")
    private Long specialtyId;

    @NotNull(message = "Ngày khám không được để trống")
    @FutureOrPresent(message = "Ngày khám không hợp lệ (phải từ hôm nay trở đi)")
    private LocalDate appointmentDate;

    @NotBlank(message = "Khung giờ khám không được để trống")
    private String timeSlot;

    private String symptoms;
}