package com.clinic.booking.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    // Chuỗi JWT Token trả về cho Front-end
    private String token;

    // Thêm trường role để Front-end biết quyền của User
    private String role;
}