package com.clinic.booking.repository;

import com.clinic.booking.entity.RetailInvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RetailInvoiceDetailRepository extends JpaRepository<RetailInvoiceDetail, Long> {
    List<RetailInvoiceDetail> findByInvoiceId(Long invoiceId);
}