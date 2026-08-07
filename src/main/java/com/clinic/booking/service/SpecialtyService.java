package com.clinic.booking.service;

import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.dto.specialty.SpecialtyRequest;
import com.clinic.booking.dto.specialty.SpecialtyResponse;
import com.clinic.booking.entity.Specialty;
import com.clinic.booking.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final com.clinic.booking.repository.DoctorRepository doctorRepository;

    // Hàm phụ trợ map Entity -> DTO
    private SpecialtyResponse mapToDTO(Specialty specialty) {
        return SpecialtyResponse.builder()
                .id(specialty.getId())
                .name(specialty.getName())
                .description(specialty.getDescription())
                .imageUrl(specialty.getImageUrl())
                .build();
    }

    // Lấy tất cả (Dùng cho cả public và admin)
    @Cacheable(value = "specialties")
    public List<SpecialtyResponse> getAllSpecialties() {
        return specialtyRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "specialty", key = "#id")
    public SpecialtyResponse getSpecialtyById(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND));
        return mapToDTO(specialty);
    }

    // THÊM CHUYÊN KHOA
    @CacheEvict(value = "specialties", allEntries = true)
    public SpecialtyResponse createSpecialty(SpecialtyRequest dto) {
        Specialty specialty = Specialty.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .build();
        Specialty savedSpecialty = specialtyRepository.save(specialty);
        return mapToDTO(savedSpecialty);
    }

    // THÊM NHIỀU CHUYÊN KHOA
    @CacheEvict(value = "specialties", allEntries = true)
    public List<SpecialtyResponse> createSpecialties(List<SpecialtyRequest> dtos) {
        List<Specialty> specialties = dtos.stream().map(dto -> Specialty.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .build()).collect(Collectors.toList());
        List<Specialty> savedSpecialties = specialtyRepository.saveAll(specialties);
        return savedSpecialties.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // CẬP NHẬT CHUYÊN KHOA
    @CacheEvict(value = {"specialties", "specialty"}, allEntries = true)
    public SpecialtyResponse updateSpecialty(Long id, SpecialtyRequest dto) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND));

        specialty.setName(dto.getName());
        specialty.setDescription(dto.getDescription());
        specialty.setImageUrl(dto.getImageUrl());

        Specialty updatedSpecialty = specialtyRepository.save(specialty);
        return mapToDTO(updatedSpecialty);
    }

    // XÓA CHUYÊN KHOA
    @CacheEvict(value = {"specialties", "specialty"}, allEntries = true)
    public void deleteSpecialty(Long id) {
        if (!specialtyRepository.existsById(id)) {
            throw new AppException(ErrorCode.SPECIALTY_NOT_FOUND);
        }
        if (doctorRepository.existsBySpecialtyId(id)) {
            throw new AppException(ErrorCode.SPECIALTY_HAS_DOCTORS);
        }
        specialtyRepository.deleteById(id);
    }
}