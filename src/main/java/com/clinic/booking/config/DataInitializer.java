package com.clinic.booking.config;

import com.clinic.booking.entity.User;
import com.clinic.booking.repository.RoleRepository;
import com.clinic.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS retail_invoice_details CASCADE");
            jdbcTemplate.execute("DROP TABLE IF EXISTS retail_invoices CASCADE");
            jdbcTemplate.execute("DROP TABLE IF EXISTS invoices CASCADE");
            System.out.println("Dropped old invoice tables successfully.");
        } catch (Exception e) {
            System.out.println("Failed to drop tables: " + e.getMessage());
        }

        String[] roles = {"ADMIN", "DOCTOR", "PATIENT", "RECEPTIONIST", "PHARMACIST"};
        for (String roleName : roles) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(com.clinic.booking.entity.Role.builder().name(roleName).build());
            }
        }

        String adminEmail = "mediproadmin@gmail.com";

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = User.builder()
                    .fullName("Quản Trị Viên Hệ Thống")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin123")) 
                    .phone("0999999999")
                    .gender("MALE")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .address("Trụ sở MediPro")
                    .status("ACTIVE")
                    .build();

            com.clinic.booking.entity.Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
            admin.setRoles(java.util.Set.of(adminRole));

            userRepository.save(admin);
            System.out.println("=========================================================");
            System.out.println("ĐÃ TẠO TÀI KHOẢN ADMIN MẶC ĐỊNH:");
            System.out.println("Email: " + adminEmail);
            System.out.println("Mật khẩu: Admin123");
            System.out.println("=========================================================");
        }
    }
}