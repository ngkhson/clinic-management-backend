package com.clinic.booking.controller;

import com.clinic.booking.dto.auth.*;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ApiResponse<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ApiResponse<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return ApiResponse.success(authenticationService.authenticate(request));
    }

    // THÊM: Gửi yêu cầu quên mật khẩu (Lấy OTP)
    @PostMapping("/forgot-password")
    public ApiResponse<Object> forgotPassword(@RequestParam String email) {
        authenticationService.forgotPassword(email);
        return ApiResponse.success("Mã OTP đã được gửi đến email của bạn.");
    }

    // THÊM: Xác nhận OTP và thiết lập mật khẩu mới
    @PostMapping("/reset-password")
    public ApiResponse<Object> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ApiResponse.success("Khôi phục mật khẩu thành công!");
    }
}