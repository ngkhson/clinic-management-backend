package com.clinic.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_services")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;           // Tên dịch vụ (VD: Siêu âm ổ bụng tổng quát nữ)

    @Column(nullable = false)
    private String category;       // Nhóm dịch vụ (VD: KHÁM BỆNH, X QUANG, SIÊU ÂM, XÉT NGHIỆM, THỦ THUẬT)

    private Double price;          // Giá dịch vụ (VNĐ)

    @Column(name = "is_active")
    private Boolean isActive;      // Trạng thái sử dụng

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}