package com.clinic.booking.dto.doctor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDoctorResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String degree;
    private String biography;
    private Long specialtyId;
    private String specialtyName;
    
    // Additional sensitive/Admin-only fields
    private String email;
    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
    private String status;
}
