package com.clinic.booking.repository;

import com.clinic.booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Tự định nghĩa thêm hàm tìm User theo email để phục vụ việc Login
    Optional<User> findByEmail(String email);

    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u JOIN u.roles r " +
            "WHERE r.name = 'PATIENT' " +
            "AND (:searchTerm IS NULL OR " +
            "  LOWER(u.fullName) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%')) " +
            "  OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%')) " +
            "  OR u.phone LIKE CONCAT('%', CAST(:searchTerm AS string), '%'))")
    org.springframework.data.domain.Page<User> findPatients(@org.springframework.data.repository.query.Param("searchTerm") String searchTerm, org.springframework.data.domain.Pageable pageable);
}