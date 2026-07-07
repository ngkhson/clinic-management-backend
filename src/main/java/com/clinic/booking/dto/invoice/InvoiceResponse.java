package com.clinic.booking.dto.invoice;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceResponse {
    private Long id;
    private Long appointmentId;
    private String patientName;
    private String doctorName;

    private Double serviceFee;
    private Double medicineFee;
    private Double totalAmount;

    private String status;
    private String paymentMethod;
    private String createdAt;
    private String paidAt;
}