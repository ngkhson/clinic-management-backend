package com.clinic.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicines")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String unit;

    private String category;

    // SỬA: Đổi int thành Integer, double thành Double để tránh lỗi Parse JSON null
    private Integer minQuantity;

    private Integer currentQuantity;

    private Double sellingPrice;

    // SỬA: Đổi boolean thành Boolean để có thể nhận giá trị null mà không sập
    @Column(name = "is_active")
    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true; // Mặc định khi tạo mới là đang sử dụng
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}