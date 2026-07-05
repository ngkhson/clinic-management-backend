package com.clinic.booking.dto.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

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
    private String diagnosis;
    private String treatmentPlan;
    private String prescription;
    private String notes;
    private boolean isDraft;

    private List<Long> serviceIds;
    private LocalDate followUpDate;
    private List<PrescriptionRequest> prescriptionDetails;

    @Data
    public static class PrescriptionRequest {
        private Long medicineId;
        private Integer quantity;
        private String dosageInstruction;
    }
}
