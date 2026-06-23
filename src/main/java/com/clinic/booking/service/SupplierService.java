package com.clinic.booking.service;

import com.clinic.booking.entity.Supplier;
import com.clinic.booking.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAllByOrderByNameAsc();
    }

    public List<Supplier> getActiveSuppliers() {
        return supplierRepository.findByIsActiveTrueOrderByNameAsc();
    }

    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier updateSupplier(Long id, Supplier details) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp!"));

        supplier.setName(details.getName());
        supplier.setContactPerson(details.getContactPerson());
        supplier.setPhone(details.getPhone());
        supplier.setEmail(details.getEmail());
        supplier.setAddress(details.getAddress());
        supplier.setTaxCode(details.getTaxCode());
        supplier.setNotes(details.getNotes());

        return supplierRepository.save(supplier);
    }

    @Transactional
    public void toggleStatus(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp!"));

        Boolean currentStatus = supplier.getIsActive();
        if (currentStatus == null) currentStatus = true;
        supplier.setIsActive(!currentStatus);

        supplierRepository.save(supplier);
    }
}