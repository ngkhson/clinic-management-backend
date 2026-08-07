package com.clinic.booking.controller;

import com.clinic.booking.dto.auth.*;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.entity.RefreshToken;
import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;
import com.clinic.booking.service.AuthenticationService;
import com.clinic.booking.service.JwtService;
import com.clinic.booking.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ApiResponse<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ApiResponse<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return ApiResponse.success(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthenticationResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtService.generateToken(user);
                    java.util.List<String> roleNames = user.getRoles().stream().map(com.clinic.booking.entity.Role::getName).toList();
                    
                    String finalRefreshToken = requestRefreshToken;
                    // TÍNH NĂNG SLIDING WINDOW CHO PATIENT
                    if (roleNames.contains("PATIENT")) {
                        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());
                        finalRefreshToken = newRefreshToken.getToken();
                    }

                    return ApiResponse.success(AuthenticationResponse.builder()
                            .token(token)
                            .refreshToken(finalRefreshToken)
                            .roles(roleNames)
                            .build());
                })
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
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