package com.clinic.booking.dto.doctor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorCreationRequest {
    // Thông tin tài khoản User
    private String email;
    private String password;
    private String fullName;
    private String address;
    private String gender;
    private LocalDate dateOfBirth;
    private String phone;

    // Thông tin Bác sĩ
    private Long specialtyId;
    private String degree;
    private String biography;
    private BigDecimal examinationPrice;
}