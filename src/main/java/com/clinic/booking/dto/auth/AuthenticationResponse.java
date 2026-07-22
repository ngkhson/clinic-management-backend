package com.clinic.booking.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    // Chuỗi JWT Token trả về cho Front-end
    private String token;
    private String refreshToken;

    // Thêm trường roles để Front-end biết danh sách quyền của User
    private List<String> roles;
    private List<String> permissions;
}