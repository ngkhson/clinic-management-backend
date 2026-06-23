package com.clinic.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "retail_invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetailInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName; // Tên khách hàng mua lẻ (Có thể để trống)

    private Double totalAmount;  // Tổng tiền thanh toán

    private LocalDateTime saleDate;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (saleDate == null) saleDate = LocalDateTime.now();
    }
}