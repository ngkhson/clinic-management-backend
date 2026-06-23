package com.clinic.booking.repository;

import com.clinic.booking.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findAllByOrderByNameAsc();
    List<Supplier> findByIsActiveTrueOrderByNameAsc();
}