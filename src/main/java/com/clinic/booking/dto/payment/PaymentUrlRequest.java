package com.clinic.booking.dto.payment;

import lombok.Data;

@Data
public class PaymentUrlRequest {
    private String targetType; // INVOICE hoặc RETAIL
    private Long targetId;
}
