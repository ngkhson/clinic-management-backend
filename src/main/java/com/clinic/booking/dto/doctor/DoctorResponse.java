package com.clinic.booking.dto.doctor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {
    private Long id;

    // Thông tin từ bảng User
    private Long userId;
    private String fullName;

    // Thông tin từ bảng Doctor
    private String degree;
    private String biography;
    private String imageUrl;
    
    private Double averageRating;
    private Long reviewCount;
    private String clinicAddress;
    private Long specialtyId;
    private String specialtyName;
}