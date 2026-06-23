package com.clinic.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceDTO {
    private Long id;
    private Long appointmentId;
    private String patientName;
    private String doctorName;

    private Double consultationFee;
    private Double serviceFee;
    private Double medicineFee;
    private Double totalAmount;

    private String status;
    private String paymentMethod;
    private String createdAt;
    private String paidAt;
}