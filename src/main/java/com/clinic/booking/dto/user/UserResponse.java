package com.clinic.booking.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
    private String status; // ACTIVE hoặc LOCKED
}