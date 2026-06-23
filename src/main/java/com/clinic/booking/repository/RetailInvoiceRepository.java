package com.clinic.booking.repository;

import com.clinic.booking.entity.RetailInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetailInvoiceRepository extends JpaRepository<RetailInvoice, Long> {
}