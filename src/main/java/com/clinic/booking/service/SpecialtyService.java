package com.clinic.booking.service;

import com.clinic.booking.dto.SpecialtyDTO;
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
    private SpecialtyDTO mapToDTO(Specialty specialty) {
        return SpecialtyDTO.builder()
                .id(specialty.getId())
                .name(specialty.getName())
                .description(specialty.getDescription())
                .imageUrl(specialty.getImageUrl())
                .build();
    }

    // Lấy tất cả (Dùng cho cả public và admin)
    public List<SpecialtyDTO> getAllSpecialties() {
        return specialtyRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // THÊM CHUYÊN KHOA
    public SpecialtyDTO createSpecialty(SpecialtyDTO dto) {
        Specialty specialty = Specialty.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .build();
        Specialty savedSpecialty = specialtyRepository.save(specialty);
        return mapToDTO(savedSpecialty);
    }

    // CẬP NHẬT CHUYÊN KHOA
    public SpecialtyDTO updateSpecialty(Long id, SpecialtyDTO dto) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên khoa!"));

        specialty.setName(dto.getName());
        specialty.setDescription(dto.getDescription());
        specialty.setImageUrl(dto.getImageUrl());

        Specialty updatedSpecialty = specialtyRepository.save(specialty);
        return mapToDTO(updatedSpecialty);
    }

    // XÓA CHUYÊN KHOA
    public void deleteSpecialty(Long id) {
        if (!specialtyRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy chuyên khoa!");
        }
        specialtyRepository.deleteById(id);
    }
}