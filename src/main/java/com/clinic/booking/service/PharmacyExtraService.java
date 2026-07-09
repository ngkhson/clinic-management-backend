package com.clinic.booking.service;

import com.clinic.booking.dto.pharmacy.PharmacyReportResponse;
import com.clinic.booking.entity.ImportInvoice;
import com.clinic.booking.entity.PharmacyNote;
import com.clinic.booking.entity.Invoice;
import com.clinic.booking.repository.ImportInvoiceRepository;
import com.clinic.booking.repository.MedicineRepository;
import com.clinic.booking.repository.PharmacyNoteRepository;
import com.clinic.booking.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PharmacyExtraService {

    private final MedicineRepository medicineRepository;
    private final ImportInvoiceRepository importInvoiceRepository;
    private final InvoiceRepository invoiceRepository;
    private final PharmacyNoteRepository pharmacyNoteRepository;

    public PharmacyReportResponse getReportSummary() {
        long totalMedicineTypes = medicineRepository.count();
        long lowStockCount = medicineRepository.findLowStockMedicines().size();

        double totalImportCost = importInvoiceRepository.findAll().stream()
                .mapToDouble(ImportInvoice::getTotalAmount)
                .sum();

        double totalRetailRevenue = invoiceRepository.findAll().stream()
                .filter(i -> "PAID".equals(i.getStatus()))
                .mapToDouble(i -> i.getMedicineFee() != null ? i.getMedicineFee() : 0.0)
                .sum();

        return PharmacyReportResponse.builder()
                .totalMedicineTypes(totalMedicineTypes)
                .lowStockCount(lowStockCount)
                .totalImportCost(totalImportCost)
                .totalRetailRevenue(totalRetailRevenue)
                .build();
    }

    public String getNote() {
        List<PharmacyNote> notes = pharmacyNoteRepository.findAll();
        return notes.isEmpty() ? "" : notes.get(0).getContent();
    }

    @Transactional
    public void saveNote(String content) {
        List<PharmacyNote> notes = pharmacyNoteRepository.findAll();
        PharmacyNote note = notes.isEmpty() ? new PharmacyNote() : notes.get(0);

        note.setContent(content);
        note.setUpdatedAt(LocalDateTime.now());
        pharmacyNoteRepository.save(note);
    }
}