package com.clinic.booking.dto.appointment;

import lombok.Data;

@Data
public class AppointmentUpdateRequest {
    private String status;
    private String symptoms;
}
