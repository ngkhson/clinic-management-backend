package com.clinic.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordDTO {
    private Long id;
    private Long appointmentId;
    private String patientName;
    private String diagnosis;
    private String treatmentPlan;
    private String prescription;
    private String notes;
    private LocalDateTime createdAt;

    // THÊM MỚI
    private List<Long> serviceIds;       // Dùng khi Bác sĩ gửi yêu cầu tạo (từ React lên)
    private List<String> serviceNames;   // Dùng để hiển thị tên dịch vụ (từ Spring Boot trả về)
}