package com.clinic.booking.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ScheduleGenerateRequest {
    private Long doctorId;
    private LocalDate date;
    private List<String> timeSlots; // Danh sách các giờ được tick chọn, VD: ["08:00", "09:00"]
    private Integer maxPatients; // Số bệnh nhân tối đa mỗi ca (mặc định cho là 1)
}