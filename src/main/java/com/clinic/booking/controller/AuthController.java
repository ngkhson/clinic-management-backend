package com.clinic.booking.controller;

import com.clinic.booking.dto.auth.AuthenticationRequest;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.dto.auth.AuthenticationResponse;
import com.clinic.booking.dto.auth.RegisterRequest;
import com.clinic.booking.dto.auth.ResetPasswordRequest;
import com.clinic.booking.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ApiResponse<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ApiResponse.success(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ApiResponse.success(authenticationService.authenticate(request));
    }

    // THÊM: Gửi yêu cầu quên mật khẩu (Lấy OTP)
    @PostMapping("/forgot-password")
    public ApiResponse<Object> forgotPassword(@RequestParam String email) {
        authenticationService.forgotPassword(email);
        return ApiResponse.success("Mã OTP đã được gửi đến email của bạn.");
    }

    @PostMapping("/reset-password")
    public ApiResponse<Object> resetPassword(@RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ApiResponse.success("Khôi phục mật khẩu thành công!");
    }

    @PatchMapping("/profile")
    public ApiResponse<Object> updateProfile(@RequestBody com.clinic.booking.dto.auth.UpdateProfileRequest request) {
        authenticationService.updateProfile(request);
        return ApiResponse.success("Cập nhật thông tin cá nhân thành công");
    }

    @PatchMapping("/change-password")
    public ApiResponse<Object> changePassword(@RequestBody com.clinic.booking.dto.auth.ChangePasswordRequest request) {
        try {
            authenticationService.changePassword(request);
            return ApiResponse.success("Đổi mật khẩu thành công");
        } catch (RuntimeException e) {
            return ApiResponse.builder().code(400).message(e.getMessage()).build();
        }
    }
}