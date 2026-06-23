package com.clinic.booking.service;

import com.clinic.booking.dto.ImportRequestDTO;
import com.clinic.booking.entity.ImportInvoice;
import com.clinic.booking.entity.ImportInvoiceDetail;
import com.clinic.booking.entity.Medicine;
import com.clinic.booking.entity.Supplier;
import com.clinic.booking.repository.ImportInvoiceDetailRepository;
import com.clinic.booking.repository.ImportInvoiceRepository;
import com.clinic.booking.repository.MedicineRepository;
import com.clinic.booking.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final ImportInvoiceRepository importInvoiceRepository;
    // Đã mở khóa và sử dụng Repository chi tiết
    private final ImportInvoiceDetailRepository importInvoiceDetailRepository;
    private final SupplierRepository supplierRepository;
    private final MedicineRepository medicineRepository;

    @Transactional
    public void createImportInvoice(ImportRequestDTO request) {
        // 1. Kiểm tra nhà cung cấp
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Nhà cung cấp!"));

        // 2. Tạo Phiếu nhập
        ImportInvoice invoice = ImportInvoice.builder()
                .supplier(supplier)
                .notes(request.getNotes())
                .totalAmount(0.0) // Sẽ cộng dồn sau
                .build();
        invoice = importInvoiceRepository.save(invoice);

        double totalAmount = 0.0;

        // 3. Duyệt qua từng chi tiết thuốc nhập
        for (ImportRequestDTO.ImportDetailDTO detailDTO : request.getDetails()) {
            Medicine medicine = medicineRepository.findById(detailDTO.getMedicineId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Thuốc ID: " + detailDTO.getMedicineId()));

            double detailTotal = detailDTO.getQuantity() * detailDTO.getImportPrice();
            totalAmount += detailTotal;

            // 4. LƯU CHI TIẾT PHIẾU NHẬP VÀO DATABASE
            ImportInvoiceDetail detail = ImportInvoiceDetail.builder()
                    .importInvoice(invoice)
                    .medicine(medicine)
                    .quantity(detailDTO.getQuantity())
                    .importPrice(detailDTO.getImportPrice())
                    .totalPrice(detailTotal)
                    .build();
            importInvoiceDetailRepository.save(detail);

            // 5. CỘNG DỒN SỐ LƯỢNG VÀO KHO
            int currentQty = medicine.getCurrentQuantity() != null ? medicine.getCurrentQuantity() : 0;
            medicine.setCurrentQuantity(currentQty + detailDTO.getQuantity());
            medicineRepository.save(medicine);
        }

        // 6. Cập nhật lại tổng tiền cho phiếu nhập chính
        invoice.setTotalAmount(totalAmount);
        importInvoiceRepository.save(invoice);
    }
}