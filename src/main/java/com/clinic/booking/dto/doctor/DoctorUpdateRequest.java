package com.clinic.booking.dto.doctor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class DoctorUpdateRequest {

    private String password;

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;

    @NotNull(message = "Vui lòng chọn chuyên khoa")
    private Long specialtyId;
    
    private String degree;
    private String imageUrl;
    private String biography;
}
