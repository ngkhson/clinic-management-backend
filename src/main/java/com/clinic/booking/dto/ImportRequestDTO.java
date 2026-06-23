package com.clinic.booking.dto;

import lombok.Data;
import java.util.List;

@Data
public class ImportRequestDTO {
    private Long supplierId;
    private String notes;
    private List<ImportDetailDTO> details;

    @Data
    public static class ImportDetailDTO {
        private Long medicineId;
        private Integer quantity;
        private Double importPrice;
    }
}