package com.clinic.booking.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleGenerateRequest {
    @NotNull(message = "Vui lòng chọn bác sĩ")
    private Long doctorId;

    @NotNull(message = "Ngày lịch khám không được để trống")
    @FutureOrPresent(message = "Ngày tạo lịch khám phải từ hôm nay trở đi")
    private LocalDate date;

    @Valid
    private List<ScheduleSlotRequest> slots; // Danh sách các giờ được tick chọn cùng với số bệnh nhân tối đa
}