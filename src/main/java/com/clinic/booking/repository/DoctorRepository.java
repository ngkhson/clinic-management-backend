package com.clinic.booking.repository;

import com.clinic.booking.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Tìm chi tiết bác sĩ dựa trên user_id (tài khoản đăng nhập)
    Optional<Doctor> findByUserId(Long userId);

    // Lấy danh sách bác sĩ thuộc về một chuyên khoa cụ thể
    List<Doctor> findBySpecialtyId(Long specialtyId);

    boolean existsBySpecialtyId(Long specialtyId);

    // Lọc theo trạng thái của user (VD: ACTIVE)
    List<Doctor> findByUserStatus(String status);

    @Query("SELECT d FROM Doctor d " +
            "JOIN d.user u " +
            "LEFT JOIN d.specialty s " +
            "WHERE (:searchTerm IS NULL OR " +
            "  LOWER(u.fullName) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%')) " +
            "  OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%')) " +
            "  OR u.phone LIKE CONCAT('%', CAST(:searchTerm AS string), '%'))")
    Page<Doctor> findDoctors(@Param("searchTerm") String searchTerm, Pageable pageable);

    List<Doctor> findBySpecialtyIdAndUserStatus(Long specialtyId, String status);

    Optional<Doctor> findByIdAndUserStatus(Long id, String status);
}