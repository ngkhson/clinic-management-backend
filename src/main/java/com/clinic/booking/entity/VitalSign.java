package com.clinic.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vital_signs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VitalSign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết trực tiếp với Lịch hẹn/Phiếu khám
    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    private Integer pulse;           // Mạch (lần/phút)
    private Double temperature;      // Nhiệt độ (°C)
    private String bloodPressure;    // Huyết áp (VD: 120/80)
    private Integer respiratoryRate; // Nhịp thở (lần/phút)
    private Double height;           // Chiều cao (cm)
    private Double weight;           // Cân nặng (kg)
    private Double bmi;              // Chỉ số khối cơ thể (BMI)

    @Column(columnDefinition = "TEXT")
    private String notes;            // Ghi chú lúc tiếp nhận

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}