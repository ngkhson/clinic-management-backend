package com.clinic.booking.controller;

import com.clinic.booking.entity.Supplier;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasAuthority('MANAGE_MEDICINE')")
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ApiResponse<List<Supplier>> getAllSuppliers() {
        return ApiResponse.success(supplierService.getAllSuppliers());
    }

    @GetMapping("/active")
    public ApiResponse<List<Supplier>> getActiveSuppliers() {
        return ApiResponse.success(supplierService.getActiveSuppliers());
    }

    @PostMapping
    public ApiResponse<Supplier> createSupplier(@RequestBody Supplier supplier) {
        return ApiResponse.success(supplierService.createSupplier(supplier));
    }

    @PutMapping("/{id}")
    public ApiResponse<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        return ApiResponse.success(supplierService.updateSupplier(id, supplier));
    }

    @PatchMapping("/{id}/toggle-status")
    public ApiResponse<Void> toggleStatus(@PathVariable Long id) {
        supplierService.toggleStatus(id);
        return ApiResponse.success();
    }
}