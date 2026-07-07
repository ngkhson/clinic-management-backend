package com.clinic.booking.service;

import com.clinic.booking.dto.auth.ChangePasswordRequest;
import com.clinic.booking.dto.user.UserProfileRequest;
import com.clinic.booking.dto.user.UserProfileResponse;
import com.clinic.booking.entity.User;
import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;
import com.clinic.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Thêm bộ mã hóa mật khẩu

    public UserProfileResponse getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return UserProfileResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .address(user.getAddress())
                .build();
    }

    public UserProfileResponse updateMyProfile(UserProfileRequest dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getGender() != null) user.setGender(dto.getGender());
        if (dto.getDateOfBirth() != null) user.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());

        userRepository.save(user);
        return getMyProfile();
    }

    // THÊM: Xử lý đổi mật khẩu cho người đang đăng nhập
    public void changePassword(ChangePasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Kiểm tra mật khẩu cũ xem có khớp với DB không
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        // Mã hóa và cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}