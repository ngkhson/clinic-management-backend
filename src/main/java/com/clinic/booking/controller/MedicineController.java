package com.clinic.booking.controller;

import com.clinic.booking.entity.Medicine;
import com.clinic.booking.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping
    public ResponseEntity<List<Medicine>> getAllMedicines() {
        return ResponseEntity.ok(medicineService.getAllMedicines());
    }

    @GetMapping("/alerts/low-stock")
    public ResponseEntity<List<Medicine>> getLowStockAlerts() {
        return ResponseEntity.ok(medicineService.getLowStockAlerts());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Medicine>> searchMedicines(@RequestParam String keyword) {
        return ResponseEntity.ok(medicineService.searchMedicines(keyword));
    }

    @PostMapping
    public ResponseEntity<Medicine> createMedicine(@RequestBody Medicine medicine) {
        return ResponseEntity.ok(medicineService.createMedicine(medicine));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medicine> updateMedicine(@PathVariable Long id, @RequestBody Medicine medicineDetails) {
        return ResponseEntity.ok(medicineService.updateMedicine(id, medicineDetails));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Void> toggleStatus(@PathVariable Long id) {
        medicineService.toggleActiveStatus(id);
        return ResponseEntity.ok().build();
    }
}