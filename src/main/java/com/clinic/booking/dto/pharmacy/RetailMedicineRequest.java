package com.clinic.booking.dto.pharmacy;

import lombok.Data;
import java.util.List;

@Data
public class RetailMedicineRequest {
    private String customerName;
    private String paymentMethod;
    private List<RetailDetailDTO> details;

    @Data
    public static class RetailDetailDTO {
        private Long medicineId;
        private Integer quantity;
        private Double unitPrice;
    }
}