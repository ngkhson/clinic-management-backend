package com.clinic.booking.repository;

import com.clinic.booking.entity.RetailInvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetailInvoiceDetailRepository extends JpaRepository<RetailInvoiceDetail, Long> {
}