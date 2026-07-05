package com.clinic.booking.dto.chat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRoomResponse {
    private String patientEmail;
    private String patientName;
    private String lastMessage;
}