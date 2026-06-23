package com.clinic.booking.repository;

import com.clinic.booking.entity.PharmacyNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyNoteRepository extends JpaRepository<PharmacyNote, Long> {
}