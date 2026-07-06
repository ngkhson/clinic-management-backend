package com.clinic.booking.controller;

import com.clinic.booking.dto.auth.ChangePasswordRequest;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.dto.user.UserProfileRequest;
import com.clinic.booking.dto.user.UserProfileResponse;
import com.clinic.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getProfile() {
        return ApiResponse.success(userService.getMyProfile());
    }

    @PutMapping("/profile")
    public ApiResponse<UserProfileResponse> updateProfile(@RequestBody UserProfileRequest request) {
        return ApiResponse.success(userService.updateMyProfile(request));
    }

    // THÊM: API Đổi mật khẩu
    @PutMapping("/change-password")
    public ApiResponse<Object> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.success("Đổi mật khẩu thành công!");
    }
}