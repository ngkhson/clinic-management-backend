package com.clinic.booking.service;

import com.clinic.booking.dto.staff.*;
import com.clinic.booking.entity.Role;
import com.clinic.booking.entity.User;
import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;
import com.clinic.booking.repository.RoleRepository;
import com.clinic.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminStaffService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<StaffResponse> getAllStaffs() {
        return userRepository.findAll().stream()
                .filter(user -> {
                    // Lọc ra những người có role ADMIN, RECEPTIONIST, PHARMACIST
                    return user.getRoles().stream().anyMatch(r -> 
                        List.of("ADMIN", "RECEPTIONIST", "PHARMACIST").contains(r.getName())
                    );
                })
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public StaffResponse createStaff(CreateStaffRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_ACTION));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode("123456")) // Mật khẩu mặc định
                .phone(request.getPhone())
                .gender(request.getGender() != null ? request.getGender() : "MALE")
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .status("ACTIVE")
                .roles(Set.of(role))
                .build();

        user = userRepository.save(user);
        return mapToDTO(user);
    }

    @Transactional
    public StaffResponse updateStaff(Long id, UpdateStaffRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_ACTION));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAddress(request.getAddress());
        user.setRoles(Set.of(role)); // Thay thế roles cũ bằng role mới

        user = userRepository.save(user);
        return mapToDTO(user);
    }

    @Transactional
    public void toggleStaffStatus(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if ("ACTIVE".equals(user.getStatus())) {
            user.setStatus("INACTIVE");
        } else {
            user.setStatus("ACTIVE");
        }
        userRepository.save(user);
    }

    private StaffResponse mapToDTO(User user) {
        String roleName = user.getRoles().isEmpty() ? "" : user.getRoles().iterator().next().getName();
        return StaffResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .address(user.getAddress())
                .status(user.getStatus())
                .roleName(roleName)
                .build();
    }
}
