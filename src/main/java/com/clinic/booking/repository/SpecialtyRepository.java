package com.clinic.booking.repository;

import com.clinic.booking.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    // JpaRepository đã cung cấp sẵn các hàm: findAll(), findById(), save(), deleteById()
}