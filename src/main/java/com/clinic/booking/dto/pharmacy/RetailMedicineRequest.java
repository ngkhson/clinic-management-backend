package com.clinic.booking.dto.pharmacy;

import lombok.Data;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;

@Data
public class RetailMedicineRequest {
    @NotBlank(message = "Tên khách hàng không được để trống")
    private String customerName;
    private String paymentMethod;

    @Valid
    private List<RetailDetailDTO> details;

    @Data
    public static class RetailDetailDTO {
        @NotNull(message = "Vui lòng chọn thuốc")
        private Long medicineId;

        @NotNull(message = "Số lượng không được để trống")
        @Min(value = 1, message = "Số lượng phải lớn hơn 0")
        private Integer quantity;

        @NotNull(message = "Đơn giá không được để trống")
        @Min(value = 0, message = "Đơn giá không được âm")
        private Double unitPrice;
    }
}