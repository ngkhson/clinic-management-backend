package com.clinic.booking.service;

import com.clinic.booking.dto.DoctorDTO;
import com.clinic.booking.entity.Doctor;
import com.clinic.booking.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    // THÊM HÀM NÀY: Lấy danh sách tất cả bác sĩ
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Lấy danh sách bác sĩ thuộc một chuyên khoa
    public List<DoctorDTO> getDoctorsBySpecialtyId(Long specialtyId) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyId(specialtyId);
        return doctors.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Lấy chi tiết 1 bác sĩ theo ID
    public DoctorDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Bác sĩ!"));
        return mapToDTO(doctor);
    }

    // Hàm phụ trợ: Chuyển đổi từ Entity sang DTO
    private DoctorDTO mapToDTO(Doctor doctor) {
        return DoctorDTO.builder()
                .id(doctor.getId())
                .userId(doctor.getUser().getId())
                .fullName(doctor.getUser().getFullName())
                .degree(doctor.getDegree())
                .biography(doctor.getBiography())
                .examinationPrice(doctor.getExaminationPrice())
                .specialtyId(doctor.getSpecialty() != null ? doctor.getSpecialty().getId() : null)
                .specialtyName(doctor.getSpecialty() != null ? doctor.getSpecialty().getName() : null)
                .build();
    }
}