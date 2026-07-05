package com.clinic.booking.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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