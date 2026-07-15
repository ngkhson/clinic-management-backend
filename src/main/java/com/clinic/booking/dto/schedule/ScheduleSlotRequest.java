package com.clinic.booking.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSlotRequest {
    @NotBlank(message = "Khung giờ không được để trống")
    private String timeSlot;

    @NotNull(message = "Số bệnh nhân tối đa không được để trống")
    @Min(value = 1, message = "Số bệnh nhân tối đa phải lớn hơn 0")
    private Integer maxPatients;
}
