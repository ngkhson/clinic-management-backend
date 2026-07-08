package com.clinic.booking.service;

import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.dto.user.UserResponse;
import com.clinic.booking.entity.User;
import com.clinic.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPatientService {

    private final UserRepository userRepository;

    public org.springframework.data.domain.Page<UserResponse> getAllPatients(int page, int size, String search) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by("id").descending());
        String searchTerm = (search == null || search.trim().isEmpty()) ? null : search.trim();
        return userRepository.findPatients(searchTerm, pageable)
                .map(this::mapToDTO);
    }

    public List<UserResponse> getPatients() {
        return userRepository.findPatientsList().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void togglePatientStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if ("ACTIVE".equals(user.getStatus())) {
            user.setStatus("LOCKED");
        } else {
            user.setStatus("ACTIVE");
        }
        userRepository.save(user);
    }

    private UserResponse mapToDTO(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .address(user.getAddress())
                .status(user.getStatus())
                .build();
    }
}