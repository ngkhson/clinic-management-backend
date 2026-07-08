package com.clinic.booking.service;

import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.entity.Medicine;
import com.clinic.booking.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineService {

    private final MedicineRepository medicineRepository;

    // THÊM MỚI: Lấy toàn bộ danh sách thuốc (kể cả đã ẩn) cho Admin xem
    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAllByOrderByNameAsc();
    }

    public org.springframework.data.domain.Page<Medicine> getMedicines(int page, int size, String search) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by("id").descending());
        String searchTerm = (search == null || search.trim().isEmpty()) ? null : search.trim();
        return medicineRepository.findMedicines(searchTerm, pageable);
    }

    // Lấy danh sách thuốc đang hoạt động
    public List<Medicine> getAllActiveMedicines() {
        return medicineRepository.findByIsActiveTrueOrderByNameAsc();
    }

    public List<Medicine> getLowStockAlerts() {
        return medicineRepository.findLowStockMedicines();
    }

    public List<Medicine> searchMedicines(String keyword) {
        return medicineRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(keyword);
    }

    @Transactional
    public Medicine createMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    @Transactional
    public Medicine updateMedicine(Long id, Medicine medicineDetails) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc với ID: " + id));

        medicine.setName(medicineDetails.getName());
        medicine.setUnit(medicineDetails.getUnit());
        medicine.setCategory(medicineDetails.getCategory());
        medicine.setMinQuantity(medicineDetails.getMinQuantity());
        medicine.setSellingPrice(medicineDetails.getSellingPrice());
        // Lưu ý: Không cập nhật currentQuantity ở đây, số lượng chỉ thay đổi qua phiếu Nhập hoặc Kê đơn

        return medicineRepository.save(medicine);
    }

    @Transactional
    public void toggleActiveStatus(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MEDICINE_NOT_FOUND));

        // Dùng getIsActive() và setIsActive() do đã đổi sang đối tượng Boolean
        Boolean currentStatus = medicine.getIsActive();
        if (currentStatus == null) {
            currentStatus = true; // Mặc định an toàn
        }
        medicine.setIsActive(!currentStatus);

        medicineRepository.save(medicine);
    }
}