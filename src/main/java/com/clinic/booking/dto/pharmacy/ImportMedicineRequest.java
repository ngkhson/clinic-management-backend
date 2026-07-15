package com.clinic.booking.dto.pharmacy;

import lombok.Data;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;

@Data
public class ImportMedicineRequest {
    @NotNull(message = "Vui lòng chọn nhà cung cấp")
    private Long supplierId;
    private String notes;

    @Valid
    private List<ImportDetailDTO> details;

    @Data
    public static class ImportDetailDTO {
        @NotNull(message = "Vui lòng chọn thuốc")
        private Long medicineId;

        @NotNull(message = "Số lượng không được để trống")
        @Min(value = 1, message = "Số lượng phải lớn hơn 0")
        private Integer quantity;

        @NotNull(message = "Giá nhập không được để trống")
        @Min(value = 0, message = "Giá nhập không được âm")
        private Double importPrice;
    }
}