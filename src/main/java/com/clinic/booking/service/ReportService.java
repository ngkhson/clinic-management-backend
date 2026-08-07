package com.clinic.booking.service;

import com.clinic.booking.dto.report.MedicineReportResponse;
import com.clinic.booking.entity.Medicine;
import com.clinic.booking.repository.MedicineRepository;
import com.clinic.booking.repository.PrescriptionDetailRepository;
import com.clinic.booking.repository.RetailInvoiceDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final MedicineRepository medicineRepository;
    private final PrescriptionDetailRepository prescriptionDetailRepository;
    private final RetailInvoiceDetailRepository retailInvoiceDetailRepository;

    public List<MedicineReportResponse> getMedicineSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Medicine> medicines = medicineRepository.findAll();
        List<MedicineReportResponse> reports = new ArrayList<>();

        for (Medicine medicine : medicines) {
            // Prescription
            Integer pQtyObj = prescriptionDetailRepository.sumQuantityByMedicineAndDateRange(medicine.getId(), startDate, endDate);
            int pQty = pQtyObj == null ? 0 : pQtyObj;
            double pRevenue = pQty * (medicine.getSellingPrice() != null ? medicine.getSellingPrice() : 0.0); // Tính bằng giá hiện tại

            // Retail
            Integer rQtyObj = retailInvoiceDetailRepository.sumQuantityByMedicineAndDateRange(medicine.getId(), startDate, endDate);
            int rQty = rQtyObj == null ? 0 : rQtyObj;

            Double rRevObj = retailInvoiceDetailRepository.sumRevenueByMedicineAndDateRange(medicine.getId(), startDate, endDate);
            double rRevenue = rRevObj == null ? 0.0 : rRevObj;

            int totalQty = pQty + rQty;
            double totalRevenue = pRevenue + rRevenue;

            if (totalQty > 0) {
                Long suppId = medicine.getSupplier() != null ? medicine.getSupplier().getId() : null;
                String suppName = medicine.getSupplier() != null ? medicine.getSupplier().getName() : null;

                reports.add(MedicineReportResponse.builder()
                        .medicineId(medicine.getId())
                        .medicineName(medicine.getName())
                        .unit(medicine.getUnit())
                        .supplierId(suppId)
                        .supplierName(suppName)
                        .prescriptionQuantity(pQty)
                        .retailQuantity(rQty)
                        .totalQuantity(totalQty)
                        .prescriptionRevenue(pRevenue)
                        .retailRevenue(rRevenue)
                        .totalRevenue(totalRevenue)
                        .build());
            }
        }
        
        return reports;
    }
}
