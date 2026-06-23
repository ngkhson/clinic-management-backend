package com.clinic.booking.service;

import com.clinic.booking.dto.RetailRequestDTO;
import com.clinic.booking.entity.Medicine;
import com.clinic.booking.entity.RetailInvoice;
import com.clinic.booking.entity.RetailInvoiceDetail;
import com.clinic.booking.repository.MedicineRepository;
import com.clinic.booking.repository.RetailInvoiceDetailRepository;
import com.clinic.booking.repository.RetailInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RetailService {

    private final RetailInvoiceRepository retailInvoiceRepository;
    private final RetailInvoiceDetailRepository retailInvoiceDetailRepository;
    private final MedicineRepository medicineRepository;

    @Transactional
    public void createRetailInvoice(RetailRequestDTO request) {
        // 1. Tạo Hóa đơn bán lẻ
        RetailInvoice invoice = RetailInvoice.builder()
                .customerName(request.getCustomerName() != null ? request.getCustomerName() : "Khách lẻ")
                .totalAmount(0.0)
                .build();
        invoice = retailInvoiceRepository.save(invoice);

        double totalAmount = 0.0;

        // 2. Duyệt qua từng loại thuốc trong giỏ hàng
        for (RetailRequestDTO.RetailDetailDTO detailDTO : request.getDetails()) {
            Medicine medicine = medicineRepository.findById(detailDTO.getMedicineId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Thuốc ID: " + detailDTO.getMedicineId()));

            // 3. KIỂM TRA TỒN KHO: Cực kỳ quan trọng
            int currentQty = medicine.getCurrentQuantity() != null ? medicine.getCurrentQuantity() : 0;
            if (currentQty < detailDTO.getQuantity()) {
                throw new RuntimeException("Thuốc '" + medicine.getName() + "' không đủ số lượng trong kho! Hiện chỉ còn: " + currentQty);
            }

            double detailTotal = detailDTO.getQuantity() * detailDTO.getUnitPrice();
            totalAmount += detailTotal;

            // 4. Lưu chi tiết hóa đơn
            RetailInvoiceDetail detail = RetailInvoiceDetail.builder()
                    .retailInvoice(invoice)
                    .medicine(medicine)
                    .quantity(detailDTO.getQuantity())
                    .unitPrice(detailDTO.getUnitPrice())
                    .totalPrice(detailTotal)
                    .build();
            retailInvoiceDetailRepository.save(detail);

            // 5. TRỪ SỐ LƯỢNG TRONG KHO
            medicine.setCurrentQuantity(currentQty - detailDTO.getQuantity());
            medicineRepository.save(medicine);
        }

        // 6. Cập nhật tổng tiền
        invoice.setTotalAmount(totalAmount);
        retailInvoiceRepository.save(invoice);
    }
}