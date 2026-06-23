package com.clinic.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "suppliers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;           // Tên công ty/nhà cung cấp

    private String contactPerson;  // Người liên hệ
    private String phone;          // Số điện thoại
    private String email;          // Email
    private String address;        // Địa chỉ
    private String taxCode;        // Mã số thuế

    @Column(columnDefinition = "TEXT")
    private String notes;          // Ghi chú thêm

    @Column(name = "is_active")
    private Boolean isActive;      // Trạng thái hợp tác

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}