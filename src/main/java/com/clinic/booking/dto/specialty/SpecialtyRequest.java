package com.clinic.booking.dto.specialty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialtyRequest {
    @NotBlank(message = "Tên chuyên khoa không được để trống")
    private String name;
    private String description;
    private String imageUrl;
}
