package com.clinic.booking.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequest {
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;
    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
}
