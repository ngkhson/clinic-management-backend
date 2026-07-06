package com.clinic.booking.dto.schedule;

import lombok.Data;

@Data
public class ScheduleSlotRequest {
    private String timeSlot;
    private Integer maxPatients;
}
