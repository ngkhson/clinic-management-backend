package com.clinic.booking.repository;

import com.clinic.booking.entity.PrescriptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface PrescriptionDetailRepository extends JpaRepository<PrescriptionDetail, Long> {
    
    @Query("SELECT SUM(pd.quantity) FROM PrescriptionDetail pd " +
           "JOIN pd.medicalRecord mr " +
           "JOIN Invoice i ON i.appointment = mr.appointment " +
           "WHERE i.status = 'PAID' AND pd.medicine.id = :medicineId " +
           "AND i.paidAt BETWEEN :startDate AND :endDate")
    Integer sumQuantityByMedicineAndDateRange(
            @Param("medicineId") Long medicineId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
}