package com.clinic.booking.service;

import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.dto.specialty.SpecialtyRequest;
import com.clinic.booking.dto.specialty.SpecialtyResponse;
import com.clinic.booking.entity.Specialty;
import com.clinic.booking.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

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
    public List<SpecialtyResponse> getAllSpecialties() {
        return specialtyRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // THÊM CHUYÊN KHOA
    public SpecialtyResponse createSpecialty(SpecialtyRequest dto) {
        Specialty specialty = Specialty.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .build();
        Specialty savedSpecialty = specialtyRepository.save(specialty);
        return mapToDTO(savedSpecialty);
    }

    // CẬP NHẬT CHUYÊN KHOA
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
    public void deleteSpecialty(Long id) {
        if (!specialtyRepository.existsById(id)) {
            throw new AppException(ErrorCode.SPECIALTY_NOT_FOUND);
        }
        specialtyRepository.deleteById(id);
    }
}