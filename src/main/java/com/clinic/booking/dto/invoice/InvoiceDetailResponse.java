package com.clinic.booking.dto.invoice;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class InvoiceDetailResponse {
    private InvoiceResponse invoice;
    private List<ServiceItem> services;
    private List<MedicineItem> medicines;

    @Data
    @Builder
    public static class ServiceItem {
        private String name;
        private Double price;
    }

    @Data
    @Builder
    public static class MedicineItem {
        private String name;
        private Integer quantity;
        private String unit;
        private Double price;
        private Double total;
    }
}
