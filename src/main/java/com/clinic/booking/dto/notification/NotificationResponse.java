package com.clinic.booking.dto.notification;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String message;
    private boolean isRead;
    private String createdAt;
}