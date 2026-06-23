package com.clinic.booking.entity;

import jakarta.persistence.*;
import lombok.*;
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

    private String diagnosis;
    private String treatmentPlan;

    // Vẫn giữ lại trường này để bác sĩ có thể gõ thêm các loại thuốc tự túc/mua ngoài (không có trong kho)
    @Column(columnDefinition = "TEXT")
    private String prescription;

    private String notes;

    // DANH SÁCH DỊCH VỤ CẬN LÂM SÀNG ĐƯỢC CHỈ ĐỊNH (Từ Module 3)
    @ManyToMany
    @JoinTable(
            name = "medical_record_services",
            joinColumns = @JoinColumn(name = "medical_record_id"),
            inverseJoinColumns = @JoinColumn(name = "medical_service_id")
    )
    private List<MedicalService> services;

    // THÊM MỚI (MODULE 5): Danh sách chi tiết Đơn thuốc điện tử (Thuốc lấy từ Kho của phòng khám)
    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrescriptionDetail> prescriptionDetails;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}