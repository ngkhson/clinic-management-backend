package com.clinic.booking.repository;

import com.clinic.booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Tự định nghĩa thêm hàm tìm User theo email để phục vụ việc Login
    Optional<User> findByEmail(String email);
}