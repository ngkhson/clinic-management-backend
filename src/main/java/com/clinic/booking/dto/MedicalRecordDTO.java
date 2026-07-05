package com.clinic.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    // --- CÁC CHỈ SỐ SINH HIỆU BỔ SUNG ---
    private Integer pulse;
    private Double temp;
    private String bp;
    private Integer resp;
    private Double height;
    private Double weight;

    // Các trường form đa bước
    private String medicalHistory;
    private String allergies;
    private String reasonForVisit;
    private String illnessHistory;
    private String clinicalSymptoms;
    private String paraclinicalResults;
    private LocalDate followUpDate;

    private String diagnosis;
    private String treatmentPlan;
    private String prescription;
    private String notes;

    private boolean isDraft;

    private List<Long> serviceIds;
    private List<String> serviceNames;
    private List<PrescriptionDetailDTO> prescriptionDetails;

    private LocalDateTime createdAt;
}