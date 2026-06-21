package com.clinic.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String senderEmail;
    private String receiverEmail; // For private messaging
    private String content;
    private String timestamp;

    private String type;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}