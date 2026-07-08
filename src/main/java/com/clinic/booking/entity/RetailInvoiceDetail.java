package com.clinic.booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "retail_invoice_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetailInvoiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    private Integer quantity;    // Số lượng bán
    private Double unitPrice;    // Giá bán tại thời điểm đó (Lấy từ bảng Medicine)
    private Double totalPrice;   // Thành tiền
}