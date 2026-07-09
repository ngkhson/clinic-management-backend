package com.clinic.booking.service;

import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.entity.MedicalService;
import com.clinic.booking.repository.MedicalServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalServiceService {

    private final MedicalServiceRepository medicalServiceRepository;

    public List<MedicalService> getAllActiveServices() {
        return medicalServiceRepository.findByIsActiveTrueOrderByNameAsc();
    }

    // THÊM HÀM NÀY: Lấy tất cả dịch vụ cho Admin
    public List<MedicalService> getAllServices() {
        return medicalServiceRepository.findAllByOrderByNameAsc();
    }

    public Page<MedicalService> getServicesPage(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return medicalServiceRepository.searchServices(search == null || search.trim().isEmpty() ? null : search.trim(), pageable);
    }

    @Transactional
    public MedicalService createService(MedicalService service) {
        return medicalServiceRepository.save(service);
    }

    @Transactional
    public List<MedicalService> createServices(List<MedicalService> services) {
        return medicalServiceRepository.saveAll(services);
    }

    @Transactional
    public MedicalService updateService(Long id, MedicalService details) {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_ACTION));

        service.setName(details.getName());
        service.setCategory(details.getCategory());
        service.setPrice(details.getPrice());

        return medicalServiceRepository.save(service);
    }

    @Transactional
    public void toggleStatus(Long id) {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_ACTION));

        Boolean currentStatus = service.getIsActive();
        if (currentStatus == null) {
            currentStatus = true;
        }
        service.setIsActive(!currentStatus);

        medicalServiceRepository.save(service);
    }
}