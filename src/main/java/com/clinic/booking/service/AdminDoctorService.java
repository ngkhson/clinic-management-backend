package com.clinic.booking.service;

import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.dto.doctor.DoctorCreationRequest;
import com.clinic.booking.dto.doctor.DoctorResponse;
import com.clinic.booking.entity.Doctor;
import com.clinic.booking.entity.Specialty;
import com.clinic.booking.entity.User;
import com.clinic.booking.repository.DoctorRepository;
import com.clinic.booking.repository.RoleRepository;
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
    private final RoleRepository roleRepository;
    private final SpecialtyRepository specialtyRepository;
    private final PasswordEncoder passwordEncoder;

    // Lấy danh sách tất cả bác sĩ cho màn hình Admin
    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // @Transactional đảm bảo nếu tạo Doctor bị lỗi thì User cũng sẽ bị hủy (Rollback)
    @Transactional
    public DoctorResponse createDoctor(DoctorCreationRequest request) {
        // 1. Kiểm tra Email đã tồn tại chưa
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // 2. Tạo tài khoản User với quyền DOCTOR
        User user = User.builder()
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .gender(request.getGender())
                .phone(request.getPhone())
                .status("ACTIVE")
                .build();
        
        com.clinic.booking.entity.Role doctorRole = roleRepository.findByName("DOCTOR")
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        user.setRoles(java.util.Set.of(doctorRole));
        
        user = userRepository.save(user);

        // 3. Tìm Chuyên khoa
        Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND));

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

    private DoctorResponse mapToDTO(Doctor doctor) {
        return DoctorResponse.builder()
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
    public DoctorResponse updateDoctor(Long id, DoctorCreationRequest request) {
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));
        User user = doctor.getUser();

        user.setFullName(request.getFullName());
        user.setAddress(request.getAddress());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        // Chỉ cập nhật mật khẩu nếu Admin có điền vào form Edit
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND));

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
            throw new AppException(ErrorCode.INVALID_ACTION);
        }
    }
}