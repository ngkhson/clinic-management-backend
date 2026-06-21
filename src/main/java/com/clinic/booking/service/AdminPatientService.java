package com.clinic.booking.service;

import com.clinic.booking.dto.UserDTO;
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

    public List<UserDTO> getAllPatients() {
        return userRepository.findAll().stream()
                .filter(u -> "PATIENT".equals(u.getRole()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void togglePatientStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh nhân"));

        if ("ACTIVE".equals(user.getStatus())) {
            user.setStatus("LOCKED");
        } else {
            user.setStatus("ACTIVE");
        }
        userRepository.save(user);
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
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