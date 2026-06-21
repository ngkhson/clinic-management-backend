package com.clinic.booking.service;

import com.clinic.booking.dto.DoctorCreationDTO;
import com.clinic.booking.dto.DoctorDTO;
import com.clinic.booking.entity.Doctor;
import com.clinic.booking.entity.Specialty;
import com.clinic.booking.entity.User;
import com.clinic.booking.repository.DoctorRepository;
import com.clinic.booking.repository.SpecialtyRepository;
import com.clinic.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDoctorService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final PasswordEncoder passwordEncoder;

    // Lấy danh sách tất cả bác sĩ cho màn hình Admin
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // @Transactional đảm bảo nếu tạo Doctor bị lỗi thì User cũng sẽ bị hủy (Rollback)
    @Transactional
    public DoctorDTO createDoctor(DoctorCreationDTO request) {
        // 1. Kiểm tra Email đã tồn tại chưa
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }

        // 2. Tạo tài khoản User với quyền DOCTOR
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role("DOCTOR")
                .status("ACTIVE")
                .build();
        user = userRepository.save(user);

        // 3. Tìm Chuyên khoa
        Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Chuyên khoa!"));

        // 4. Tạo Hồ sơ Bác sĩ
        Doctor doctor = Doctor.builder()
                .user(user)
                .specialty(specialty)
                .degree(request.getDegree())
                .biography(request.getBiography())
                .examinationPrice(request.getExaminationPrice())
                .build();
        doctor = doctorRepository.save(doctor);

        return mapToDTO(doctor);
    }

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

    @Transactional
    public DoctorDTO updateDoctor(Long id, DoctorCreationDTO request) {
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));
        User user = doctor.getUser();

        user.setFullName(request.getFullName());
        // Chỉ cập nhật mật khẩu nếu Admin có điền vào form Edit
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Chuyên khoa!"));

        doctor.setSpecialty(specialty);
        doctor.setDegree(request.getDegree());
        doctor.setBiography(request.getBiography());
        doctor.setExaminationPrice(request.getExaminationPrice());

        userRepository.save(user);
        doctor = doctorRepository.save(doctor);
        return mapToDTO(doctor);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        try {
            Doctor doctor = doctorRepository.findById(id).orElseThrow();
            User user = doctor.getUser();
            doctorRepository.delete(doctor);
            userRepository.delete(user);
        } catch (Exception e) {
            throw new RuntimeException("Không thể xóa bác sĩ này do họ đã có dữ liệu Lịch khám trong hệ thống!");
        }
    }
}