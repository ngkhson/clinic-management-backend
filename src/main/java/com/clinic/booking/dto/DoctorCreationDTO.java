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
public class DoctorCreationDTO {
    // Thông tin tài khoản User
    private String email;
    private String password;
    private String fullName;

    // Thông tin Bác sĩ
    private Long specialtyId;
    private String degree;
    private String biography;
    private BigDecimal examinationPrice;
}