package com.clinic.booking.dto.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordRequest {
    private Long appointmentId;
    private Integer pulse;
    private Double temp;
    private String bp;
    private Integer resp;
    private Double height;
    private Double weight;
    
    private String medicalHistory;
    private String allergies;
    private String reasonForVisit;
    private String illnessHistory;
    private String clinicalSymptoms;
    private String paraclinicalResults;

    @NotBlank(message = "Chẩn đoán không được để trống")
    private String diagnosis;
    private String treatmentPlan;
    private String prescription;
    private String notes;
    private boolean isDraft;

    private List<Long> serviceIds;
    private LocalDate followUpDate;
    
    @Valid
    private List<PrescriptionRequest> prescriptionDetails;

    @Data
    public static class PrescriptionRequest {
        @NotNull(message = "Thuốc không được để trống")
        private Long medicineId;

        @NotNull(message = "Số lượng không được để trống")
        @Min(value = 1, message = "Số lượng phải lớn hơn 0")
        private Integer quantity;

        @NotBlank(message = "Cách dùng không được để trống")
        private String dosageInstruction;
    }
}
