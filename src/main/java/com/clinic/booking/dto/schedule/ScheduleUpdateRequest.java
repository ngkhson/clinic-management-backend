package com.clinic.booking.dto.schedule;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ScheduleUpdateRequest {
    private LocalDate workDate;
    private String timeSlot;
    private Integer maxPatients;
}
