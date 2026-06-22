package com.clinic.booking.service;

import com.clinic.booking.entity.MedicalService;
import com.clinic.booking.repository.MedicalServiceRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public MedicalService createService(MedicalService service) {
        return medicalServiceRepository.save(service);
    }

    @Transactional
    public MedicalService updateService(Long id, MedicalService details) {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ y tế này!"));

        service.setName(details.getName());
        service.setCategory(details.getCategory());
        service.setPrice(details.getPrice());

        return medicalServiceRepository.save(service);
    }

    @Transactional
    public void toggleStatus(Long id) {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ y tế này!"));

        Boolean currentStatus = service.getIsActive();
        if (currentStatus == null) {
            currentStatus = true;
        }
        service.setIsActive(!currentStatus);

        medicalServiceRepository.save(service);
    }
}