package com.clinic.booking.dto;

import lombok.Data;
import java.util.List;

@Data
public class RetailRequestDTO {
    private String customerName;
    private List<RetailDetailDTO> details;

    @Data
    public static class RetailDetailDTO {
        private Long medicineId;
        private Integer quantity;
        private Double unitPrice;
    }
}