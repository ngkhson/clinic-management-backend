package com.clinic.booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "import_invoice_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportInvoiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "import_invoice_id", nullable = false)
    private ImportInvoice importInvoice;

    @ManyToOne
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    private Integer quantity;    // Số lượng nhập
    private Double importPrice;  // Giá nhập / 1 đơn vị
    private Double totalPrice;   // Thành tiền (quantity * importPrice)
}