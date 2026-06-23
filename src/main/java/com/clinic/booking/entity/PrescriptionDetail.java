package com.clinic.booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prescription_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @ManyToOne
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    private Integer quantity; // Số lượng cấp phát
    private String dosageInstruction; // Hướng dẫn sử dụng (VD: Uống sáng 1 viên, tối 1 viên)
}