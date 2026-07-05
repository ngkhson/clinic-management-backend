package com.clinic.booking.controller;

import com.clinic.booking.dto.auth.ChangePasswordRequest;
import com.clinic.booking.dto.user.UserProfileRequest;
import com.clinic.booking.dto.user.UserProfileResponse;
import com.clinic.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@RequestBody UserProfileRequest request) {
        return ResponseEntity.ok(userService.updateMyProfile(request));
    }

    // THÊM: API Đổi mật khẩu
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok("Đổi mật khẩu thành công!");
    }
}