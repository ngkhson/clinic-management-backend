package com.clinic.booking.repository;

import com.clinic.booking.entity.MedicalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {
    List<MedicalService> findByIsActiveTrueOrderByNameAsc();
    List<MedicalService> findByCategoryAndIsActiveTrueOrderByNameAsc(String category);
}