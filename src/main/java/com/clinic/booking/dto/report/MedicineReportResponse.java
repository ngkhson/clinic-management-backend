package com.clinic.booking.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineReportResponse {
    private Long medicineId;
    private String medicineName;
    private String unit;
    private Long supplierId;
    private String supplierName;
    
    private int prescriptionQuantity;
    private int retailQuantity;
    private int totalQuantity;
    
    private double prescriptionRevenue;
    private double retailRevenue;
    private double totalRevenue;
}
