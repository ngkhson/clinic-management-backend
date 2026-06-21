package com.clinic.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDTO {
    private Long id;
    private String message;
    private boolean isRead;
    private String createdAt;
}