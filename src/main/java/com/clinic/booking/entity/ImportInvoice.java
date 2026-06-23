package com.clinic.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "import_invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    private Double totalAmount; // Tổng tiền phiếu nhập
    private String notes;       // Ghi chú

    private LocalDateTime importDate;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (importDate == null) importDate = LocalDateTime.now();
    }
}