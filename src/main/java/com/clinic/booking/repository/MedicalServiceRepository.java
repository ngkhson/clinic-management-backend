package com.clinic.booking.repository;

import com.clinic.booking.entity.MedicalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {
    List<MedicalService> findByIsActiveTrueOrderByNameAsc();
    List<MedicalService> findByCategoryAndIsActiveTrueOrderByNameAsc(String category);
    // THÊM HÀM NÀY: Lấy tất cả dịch vụ (kể cả đã tắt)
    List<MedicalService> findAllByOrderByNameAsc();

    @Query("SELECT s FROM MedicalService s WHERE " +
            "(:searchTerm IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%')))")
    Page<MedicalService> searchServices(@Param("searchTerm") String searchTerm, Pageable pageable);
}