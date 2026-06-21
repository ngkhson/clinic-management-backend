package com.clinic.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private String fullName;
    private String email; // Sẽ disable ở Front-end không cho sửa
    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
}