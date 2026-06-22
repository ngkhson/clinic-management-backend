package com.clinic.booking.repository;

import com.clinic.booking.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    // THÊM MỚI: Lấy tất cả thuốc (cho Admin xem toàn bộ kho kể cả thuốc bị ẩn/tạm ngưng)
    List<Medicine> findAllByOrderByNameAsc();

    // Lấy tất cả thuốc đang được sử dụng (Dùng cho giao diện Kê đơn của Bác sĩ)
    List<Medicine> findByIsActiveTrueOrderByNameAsc();

    // Tính năng: Lọc danh sách thuốc sắp hết (Hiện còn <= Cơ số)
    @Query("SELECT m FROM Medicine m WHERE m.currentQuantity <= m.minQuantity AND m.isActive = true ORDER BY m.currentQuantity ASC")
    List<Medicine> findLowStockMedicines();

    // Tìm kiếm thuốc theo tên (Dùng cho thanh Search)
    List<Medicine> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
}