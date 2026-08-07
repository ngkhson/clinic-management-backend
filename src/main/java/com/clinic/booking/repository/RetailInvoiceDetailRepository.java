package com.clinic.booking.repository;

import com.clinic.booking.entity.RetailInvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RetailInvoiceDetailRepository extends JpaRepository<RetailInvoiceDetail, Long> {
    List<RetailInvoiceDetail> findByInvoiceId(Long invoiceId);

    @Query("SELECT SUM(r.quantity) FROM RetailInvoiceDetail r " +
           "WHERE r.medicine.id = :medicineId AND r.invoice.status = 'PAID' " +
           "AND r.invoice.paidAt BETWEEN :startDate AND :endDate")
    Integer sumQuantityByMedicineAndDateRange(
            @Param("medicineId") Long medicineId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(r.totalPrice) FROM RetailInvoiceDetail r " +
           "WHERE r.medicine.id = :medicineId AND r.invoice.status = 'PAID' " +
           "AND r.invoice.paidAt BETWEEN :startDate AND :endDate")
    Double sumRevenueByMedicineAndDateRange(
            @Param("medicineId") Long medicineId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
}