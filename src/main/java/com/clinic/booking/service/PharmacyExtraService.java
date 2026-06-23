package com.clinic.booking.service;

import com.clinic.booking.dto.PharmacyReportDTO;
import com.clinic.booking.entity.ImportInvoice;
import com.clinic.booking.entity.PharmacyNote;
import com.clinic.booking.entity.RetailInvoice;
import com.clinic.booking.repository.ImportInvoiceRepository;
import com.clinic.booking.repository.MedicineRepository;
import com.clinic.booking.repository.PharmacyNoteRepository;
import com.clinic.booking.repository.RetailInvoiceRepository;
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
    private final RetailInvoiceRepository retailInvoiceRepository;
    private final PharmacyNoteRepository pharmacyNoteRepository;

    // --- LOGIC BÁO CÁO THỐNG KÊ ---
    public PharmacyReportDTO getReportSummary() {
        long totalMedicineTypes = medicineRepository.count();
        long lowStockCount = medicineRepository.findLowStockMedicines().size();

        double totalImportCost = importInvoiceRepository.findAll().stream()
                .mapToDouble(ImportInvoice::getTotalAmount)
                .sum();

        double totalRetailRevenue = retailInvoiceRepository.findAll().stream()
                .mapToDouble(RetailInvoice::getTotalAmount)
                .sum();

        return PharmacyReportDTO.builder()
                .totalMedicineTypes(totalMedicineTypes)
                .lowStockCount(lowStockCount)
                .totalImportCost(totalImportCost)
                .totalRetailRevenue(totalRetailRevenue)
                .build();
    }

    // --- LOGIC SỔ GHI CHÚ ---
    public String getNote() {
        List<PharmacyNote> notes = pharmacyNoteRepository.findAll();
        return notes.isEmpty() ? "" : notes.get(0).getContent();
    }

    @Transactional
    public void saveNote(String content) {
        List<PharmacyNote> notes = pharmacyNoteRepository.findAll();
        // Luôn chỉ dùng 1 bản ghi duy nhất (dòng đầu tiên) để làm sổ tay dùng chung
        PharmacyNote note = notes.isEmpty() ? new PharmacyNote() : notes.get(0);

        note.setContent(content);
        note.setUpdatedAt(LocalDateTime.now());
        pharmacyNoteRepository.save(note);
    }
}