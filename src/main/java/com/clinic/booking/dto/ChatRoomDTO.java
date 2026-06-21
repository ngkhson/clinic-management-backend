package com.clinic.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRoomDTO {
    private String patientEmail;
    private String patientName;
    private String lastMessage;
}