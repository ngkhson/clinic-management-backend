package com.clinic.booking.repository;

import com.clinic.booking.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByAppointmentId(Long appointmentId);
    List<Invoice> findAllByOrderByCreatedAtDesc();
    List<Invoice> findByType(String type);

    @Query("SELECT i FROM Invoice i " +
           "LEFT JOIN i.appointment a " +
           "LEFT JOIN a.patient p " +
           "WHERE i.status = :status " +
           "AND (:type IS NULL OR i.type = :type) " +
           "AND (:paymentMethod IS NULL OR i.paymentMethod = :paymentMethod) " +
           "AND (:searchTerm IS NULL OR " +
           "  LOWER(COALESCE(p.fullName, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "  OR LOWER(COALESCE(i.customerName, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "  OR CAST(i.id AS string) LIKE CONCAT('%', :searchTerm, '%')" +
           ")")
    Page<Invoice> findInvoiceHistory(
            @Param("status") String status,
            @Param("type") String type,
            @Param("paymentMethod") String paymentMethod,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );
}