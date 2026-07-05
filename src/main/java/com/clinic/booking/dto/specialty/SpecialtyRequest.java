package com.clinic.booking.dto.specialty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialtyRequest {
    private String name;
    private String description;
    private String imageUrl;
}
