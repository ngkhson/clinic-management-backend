package com.clinic.booking.dto;

import lombok.Data;

@Data
public class AdminReceptionRequest {
    // THÊM TRƯỜNG NÀY ĐỂ XỬ LÝ KHÁCH ĐÃ ĐẶT LỊCH TRƯỚC
    private Long appointmentId;

    // Thông tin Phiếu khám (Dùng cho khách vãng lai)
    private Long patientId;
    private Long doctorId;
    private Long scheduleId;
    private String symptoms;

    // Thông tin Sinh hiệu (Có thể null nếu không đo)
    private Integer pulse;
    private Double temperature;
    private String bloodPressure;
    private Integer respiratoryRate;
    private Double height;
    private Double weight;
    private Double bmi;
    private String notes;
}