package com.clinic.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    private Long id;

    // Thông tin từ bảng User
    private Long userId;
    private String fullName;

    // Thông tin từ bảng Doctor
    private String degree;
    private String biography;
    private BigDecimal examinationPrice;

    // Thông tin từ bảng Specialty
    private Long specialtyId;
    private String specialtyName;
}