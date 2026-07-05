package com.clinic.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "medical_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    // --- CÁC CHỈ SỐ SINH HIỆU BỔ SUNG ---
    private Integer pulse;           // Mạch (lần/phút)
    private Double temp;             // Nhiệt độ (°C)
    private String bp;               // Huyết áp (VD: 120/80)
    private Integer resp;            // Nhịp thở (lần/phút)
    private Double height;           // Chiều cao (cm)
    private Double weight;           // Cân nặng (kg)

    // Sinh hiệu & Tiền sử khác
    @Column(columnDefinition = "TEXT") private String medicalHistory;
    @Column(columnDefinition = "TEXT") private String allergies;

    // Lâm sàng
    @Column(columnDefinition = "TEXT") private String reasonForVisit;
    @Column(columnDefinition = "TEXT") private String illnessHistory;
    @Column(columnDefinition = "TEXT") private String clinicalSymptoms;

    // Cận lâm sàng & Chẩn đoán
    @Column(columnDefinition = "TEXT") private String paraclinicalResults;
    private String diagnosis;
    private String treatmentPlan;

    // Kê toa & Dặn dò
    @Column(columnDefinition = "TEXT")
    private String prescription; // Thuốc mua ngoài
    private String notes;
    private LocalDate followUpDate; // Ngày tái khám

    @ManyToMany
    @JoinTable(
            name = "medical_record_services",
            joinColumns = @JoinColumn(name = "medical_record_id"),
            inverseJoinColumns = @JoinColumn(name = "medical_service_id")
    )
    private List<MedicalService> services;

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrescriptionDetail> prescriptionDetails;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}