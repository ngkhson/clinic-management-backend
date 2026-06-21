package com.clinic.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private Long id;
    private Long doctorId;
    private LocalDate workDate;
    private String timeSlot;
    private Integer maxPatients;
    private Integer currentPatients;

    // Thuộc tính tính toán để Front-end biết khung giờ này còn đặt được không
    private boolean isAvailable;
}