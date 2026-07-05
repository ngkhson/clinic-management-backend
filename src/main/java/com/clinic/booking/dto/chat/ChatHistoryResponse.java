package com.clinic.booking.dto.chat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatHistoryResponse {
    private String senderEmail;
    private String receiverEmail;
    private String content;
    private String timestamp;
    private String type; // Thêm trường type để tương thích với Frontend
}