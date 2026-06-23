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

    // Thuốc kê tay/tự túc (nhập text)
    private String prescription;

    private String notes;
    private LocalDateTime createdAt;

    // --- MODULE 3: DỊCH VỤ CẬN LÂM SÀNG ---
    private List<Long> serviceIds;       // Dùng khi Bác sĩ gửi yêu cầu tạo (từ React lên)
    private List<String> serviceNames;   // Dùng để hiển thị tên dịch vụ (từ Spring Boot trả về)

    // --- MODULE 5: ĐƠN THUỐC ĐIỆN TỬ (TRỪ KHO) ---
    private List<PrescriptionDetailDTO> prescriptionDetails;
}