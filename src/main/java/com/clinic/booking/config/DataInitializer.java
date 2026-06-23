package com.clinic.booking.config;

import com.clinic.booking.entity.User;
import com.clinic.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem tài khoản admin đã tồn tại trong Database chưa
        String adminEmail = "mediproadmin@gmail.com";

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = User.builder()
                    .fullName("Quản Trị Viên Hệ Thống")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin123")) // Mật khẩu mặc định
                    .phone("0999999999")
                    .gender("MALE")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .address("Trụ sở MediPro")
                    .role("ADMIN")
                    .status("ACTIVE")
                    .build();

            userRepository.save(admin);
            System.out.println("=========================================================");
            System.out.println("ĐÃ TẠO TÀI KHOẢN ADMIN MẶC ĐỊNH:");
            System.out.println("Email: " + adminEmail);
            System.out.println("Mật khẩu: admin123");
            System.out.println("=========================================================");
        }
    }
}