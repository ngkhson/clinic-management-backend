package com.clinic.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDetailDTO {
    private Long id;
    private Long medicineId;
    private String medicineName;
    private String unit;
    private Integer quantity;
    private String dosageInstruction;
}