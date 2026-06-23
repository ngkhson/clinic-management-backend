package com.clinic.booking.repository;

import com.clinic.booking.entity.ImportInvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportInvoiceDetailRepository extends JpaRepository<ImportInvoiceDetail, Long> {
}