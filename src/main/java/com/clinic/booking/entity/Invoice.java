package com.clinic.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    private Double consultationFee; // Phí khám bệnh
    private Double serviceFee;      // Phí cận lâm sàng
    private Double medicineFee;     // Phí thuốc
    private Double totalAmount;     // Tổng cộng

    private String status;          // UNPAID (Chưa thanh toán), PAID (Đã thanh toán)
    private String paymentMethod;   // CASH (Tiền mặt), TRANSFER (Chuyển khoản)

    private LocalDateTime createdAt;
    private LocalDateTime paidAt;   // Thời gian thanh toán

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "UNPAID";
    }
}