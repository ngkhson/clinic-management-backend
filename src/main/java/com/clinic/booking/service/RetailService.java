package com.clinic.booking.service;

import com.clinic.booking.dto.pharmacy.RetailMedicineRequest;
import com.clinic.booking.entity.Medicine;
import com.clinic.booking.entity.Invoice;
import com.clinic.booking.entity.RetailInvoiceDetail;
import com.clinic.booking.repository.MedicineRepository;
import com.clinic.booking.repository.RetailInvoiceDetailRepository;
import com.clinic.booking.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RetailService {

    private final InvoiceRepository invoiceRepository;
    private final RetailInvoiceDetailRepository retailInvoiceDetailRepository;
    private final MedicineRepository medicineRepository;
    private final InvoiceService invoiceService; // Inject to use payInvoice

    public java.util.List<Invoice> getAllRetailInvoices() {
        return invoiceRepository.findByType("RETAIL");
    }

    @Transactional
    public Invoice createRetailInvoice(RetailMedicineRequest request) {
        String paymentMethod = request.getPaymentMethod() != null ? request.getPaymentMethod() : "CASH";
        
        Invoice invoice = Invoice.builder()
                .customerName(request.getCustomerName() != null && !request.getCustomerName().isEmpty() ? request.getCustomerName() : "Khách lẻ")
                .paymentMethod(paymentMethod)
                .status("CASH".equalsIgnoreCase(paymentMethod) ? "PAID" : "UNPAID")
                .type("RETAIL")
                .serviceFee(0.0)
                .medicineFee(0.0)
                .totalAmount(0.0)
                .build();
        invoice = invoiceRepository.save(invoice);

        double totalAmount = 0.0;

        for (RetailMedicineRequest.RetailDetailDTO detailDTO : request.getDetails()) {
            Medicine medicine = medicineRepository.findById(detailDTO.getMedicineId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Thuốc ID: " + detailDTO.getMedicineId()));

            int currentQty = medicine.getCurrentQuantity() != null ? medicine.getCurrentQuantity() : 0;
            if (currentQty < detailDTO.getQuantity()) {
                throw new RuntimeException("Thuốc '" + medicine.getName() + "' không đủ số lượng trong kho! Hiện chỉ còn: " + currentQty);
            }

            double detailTotal = detailDTO.getQuantity() * detailDTO.getUnitPrice();
            totalAmount += detailTotal;

            RetailInvoiceDetail detail = RetailInvoiceDetail.builder()
                    .invoice(invoice)
                    .medicine(medicine)
                    .quantity(detailDTO.getQuantity())
                    .unitPrice(detailDTO.getUnitPrice())
                    .totalPrice(detailTotal)
                    .build();
            retailInvoiceDetailRepository.save(detail);

            medicine.setCurrentQuantity(currentQty - detailDTO.getQuantity());
            medicineRepository.save(medicine);
        }

        invoice.setMedicineFee(totalAmount);
        invoice.setTotalAmount(totalAmount);
        
        if ("PAID".equals(invoice.getStatus())) {
            invoice.setPaidAt(java.time.LocalDateTime.now());
        }
        
        return invoiceRepository.save(invoice);
    }
}