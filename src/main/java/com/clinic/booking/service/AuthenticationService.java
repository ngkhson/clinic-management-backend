package com.clinic.booking.service;

import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.dto.auth.AuthenticationRequest;
import com.clinic.booking.dto.auth.AuthenticationResponse;
import com.clinic.booking.dto.auth.RegisterRequest;
import com.clinic.booking.dto.auth.ResetPasswordRequest;
import com.clinic.booking.entity.User;
import com.clinic.booking.repository.RoleRepository;
import com.clinic.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // THÊM: Công cụ gửi Mail của Spring Boot
    private final JavaMailSender mailSender;

    // Lưu trữ OTP tạm thời trong RAM (Trong thực tế có thể dùng Redis để thiết lập thời gian hết hạn)
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .status("ACTIVE")
                .build();
        
        com.clinic.booking.entity.Role patientRole = roleRepository.findByName("PATIENT")
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)); // Handle gracefully in real app
        user.setRoles(Set.of(patientRole));
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        List<String> roleNames = user.getRoles().stream().map(com.clinic.booking.entity.Role::getName).toList();
        return AuthenticationResponse.builder().token(jwtToken).roles(roleNames).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        List<String> roleNames = user.getRoles().stream().map(com.clinic.booking.entity.Role::getName).toList();
        return AuthenticationResponse.builder().token(jwtToken).roles(roleNames).build();
    }

    // --- XỬ LÝ QUÊN MẬT KHẨU BẰNG EMAIL THẬT ---
    public void forgotPassword(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Sinh mã OTP 6 số ngẫu nhiên
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, otp);

        // Gọi hàm gửi Email
        sendOtpEmail(email, otp);
    }

    public void resetPassword(ResetPasswordRequest request) {
        String storedOtp = otpStorage.get(request.getEmail());
        if (storedOtp != null && storedOtp.equals(request.getOtp())) {
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            // Xóa OTP sau khi dùng thành công
            otpStorage.remove(request.getEmail());
        } else {
            throw new AppException(ErrorCode.INVALID_KEY);
        }
    }

    // HÀM PHỤ TRỢ: Cấu hình nội dung Email và gửi đi
    private void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Mã OTP Khôi Phục Mật Khẩu - Hệ Thống MediPro");
            message.setText("Xin chào,\n\n"
                    + "Chúng tôi nhận được yêu cầu khôi phục mật khẩu từ tài khoản của bạn.\n"
                    + "Mã OTP xác nhận của bạn là: " + otp + "\n\n"
                    + "Lưu ý: Mã này chỉ có giá trị sử dụng một lần. Vui lòng không chia sẻ mã này cho bất kỳ ai để đảm bảo an toàn cho tài khoản của bạn.\n\n"
                    + "Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này hoặc liên hệ ngay với bộ phận hỗ trợ.\n\n"
                    + "Trân trọng,\n"
                    + "Đội ngũ Phòng khám MediPro.");

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email: " + e.getMessage());
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public void updateProfile(com.clinic.booking.dto.auth.UpdateProfileRequest request) {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getAddress() != null) user.setAddress(request.getAddress());

        userRepository.save(user);
    }

    public void changePassword(com.clinic.booking.dto.auth.ChangePasswordRequest request) {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_KEY); // Or a specific WRONG_PASSWORD error code
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}