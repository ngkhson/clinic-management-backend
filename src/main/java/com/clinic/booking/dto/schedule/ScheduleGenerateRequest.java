package com.clinic.booking.dto.schedule;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ScheduleGenerateRequest {
    private Long doctorId;
    private LocalDate date;
    private List<ScheduleSlotRequest> slots; // Danh sách các giờ được tick chọn cùng với số bệnh nhân tối đa
}