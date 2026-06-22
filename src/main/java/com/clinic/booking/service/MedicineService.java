package com.clinic.booking.service;

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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc!"));

        // SỬA: Dùng getIsActive() và setIsActive() do đã đổi sang đối tượng Boolean
        Boolean currentStatus = medicine.getIsActive();
        if (currentStatus == null) {
            currentStatus = true; // Mặc định an toàn
        }
        medicine.setIsActive(!currentStatus);

        medicineRepository.save(medicine);
    }
}