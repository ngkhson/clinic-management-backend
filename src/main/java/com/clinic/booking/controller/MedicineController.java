package com.clinic.booking.controller;

import com.clinic.booking.entity.Medicine;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_MEDICINE') or hasAuthority('DISPENSE_MEDICINE')")
    public ApiResponse<Page<Medicine>> getMedicines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ApiResponse.success(medicineService.getMedicines(page, size, search));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_MEDICINE') or hasAuthority('DISPENSE_MEDICINE')")
    public ApiResponse<List<Medicine>> getAllMedicines() {
        return ApiResponse.success(medicineService.getAllMedicines());
    }

    @GetMapping("/alerts/low-stock")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_MEDICINE')")
    public ApiResponse<List<Medicine>> getLowStockAlerts() {
        return ApiResponse.success(medicineService.getLowStockAlerts());
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_MEDICINE') or hasAuthority('DISPENSE_MEDICINE')")
    public ApiResponse<List<Medicine>> searchMedicines(@RequestParam String keyword) {
        return ApiResponse.success(medicineService.searchMedicines(keyword));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_MEDICINE')")
    public ApiResponse<Medicine> createMedicine(@RequestBody Medicine medicine) {
        return ApiResponse.success(medicineService.createMedicine(medicine));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_MEDICINE')")
    public ApiResponse<Medicine> updateMedicine(@PathVariable Long id, @RequestBody Medicine medicineDetails) {
        return ApiResponse.success(medicineService.updateMedicine(id, medicineDetails));
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_MEDICINE')")
    public ApiResponse<Void> toggleStatus(@PathVariable Long id) {
        medicineService.toggleActiveStatus(id);
        return ApiResponse.success();
    }
}