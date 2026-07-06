package com.clinic.booking.controller;

import com.clinic.booking.entity.Medicine;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping
    public ApiResponse<List<Medicine>> getAllMedicines() {
        return ApiResponse.success(medicineService.getAllMedicines());
    }

    @GetMapping("/alerts/low-stock")
    public ApiResponse<List<Medicine>> getLowStockAlerts() {
        return ApiResponse.success(medicineService.getLowStockAlerts());
    }

    @GetMapping("/search")
    public ApiResponse<List<Medicine>> searchMedicines(@RequestParam String keyword) {
        return ApiResponse.success(medicineService.searchMedicines(keyword));
    }

    @PostMapping
    public ApiResponse<Medicine> createMedicine(@RequestBody Medicine medicine) {
        return ApiResponse.success(medicineService.createMedicine(medicine));
    }

    @PutMapping("/{id}")
    public ApiResponse<Medicine> updateMedicine(@PathVariable Long id, @RequestBody Medicine medicineDetails) {
        return ApiResponse.success(medicineService.updateMedicine(id, medicineDetails));
    }

    @PatchMapping("/{id}/toggle-status")
    public ApiResponse<Void> toggleStatus(@PathVariable Long id) {
        medicineService.toggleActiveStatus(id);
        return ApiResponse.success();
    }
}