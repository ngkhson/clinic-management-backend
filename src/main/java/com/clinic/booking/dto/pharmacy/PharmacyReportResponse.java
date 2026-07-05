package com.clinic.booking.dto.pharmacy;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PharmacyReportResponse {
    private long totalMedicineTypes;  // Tổng số loại thuốc trong kho
    private long lowStockCount;       // Số lượng thuốc sắp hết
    private double totalImportCost;   // Tổng chi phí nhập kho
    private double totalRetailRevenue; // Tổng doanh thu bán lẻ
}