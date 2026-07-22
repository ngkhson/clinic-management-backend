package com.clinic.booking.config;

import com.clinic.booking.entity.User;
import com.clinic.booking.repository.RoleRepository;
import com.clinic.booking.repository.UserRepository;
import com.clinic.booking.repository.PermissionRepository;
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
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {

        String[] roles = {"ADMIN", "DOCTOR", "PATIENT", "RECEPTIONIST", "PHARMACIST"};
        for (String roleName : roles) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(com.clinic.booking.entity.Role.builder().name(roleName).build());
            }
        }

        // --- TẠO CÁC PERMISSIONS ---
        String[] permissionNames = {
            "MANAGE_APPOINTMENT", "RECEPTION_PATIENT", "VIEW_PATIENT_LIST", // Lễ tân
            "EXAMINE_PATIENT", "VIEW_MEDICAL_RECORD", // Bác sĩ
            "MANAGE_MEDICINE", "DISPENSE_MEDICINE", // Kho & Dược
            "MANAGE_BILLING", // Thu ngân
            "MANAGE_SYSTEM" // Hệ thống
        };

        for (String pName : permissionNames) {
            if (permissionRepository.findByName(pName).isEmpty()) {
                permissionRepository.save(com.clinic.booking.entity.Permission.builder().name(pName).description("Quyền " + pName).build());
            }
        }

        // Tự động gán quyền cho RECEPTIONIST
        com.clinic.booking.entity.Role receptionistRole = roleRepository.findByName("RECEPTIONIST").orElse(null);
        if (receptionistRole != null) {
            java.util.Set<com.clinic.booking.entity.Permission> perms = new java.util.HashSet<>();
            permissionRepository.findByName("MANAGE_APPOINTMENT").ifPresent(perms::add);
            permissionRepository.findByName("RECEPTION_PATIENT").ifPresent(perms::add);
            permissionRepository.findByName("VIEW_PATIENT_LIST").ifPresent(perms::add);
            permissionRepository.findByName("MANAGE_BILLING").ifPresent(perms::add);
            receptionistRole.setPermissions(perms);
            roleRepository.save(receptionistRole);
        }

        // Tự động gán quyền cho PHARMACIST
        com.clinic.booking.entity.Role pharmacistRole = roleRepository.findByName("PHARMACIST").orElse(null);
        if (pharmacistRole != null) {
            java.util.Set<com.clinic.booking.entity.Permission> perms = new java.util.HashSet<>();
            permissionRepository.findByName("MANAGE_MEDICINE").ifPresent(perms::add);
            permissionRepository.findByName("DISPENSE_MEDICINE").ifPresent(perms::add);
            permissionRepository.findByName("VIEW_MEDICAL_RECORD").ifPresent(perms::add);
            permissionRepository.findByName("VIEW_PATIENT_LIST").ifPresent(perms::add);
            permissionRepository.findByName("MANAGE_BILLING").ifPresent(perms::add);
            pharmacistRole.setPermissions(perms);
            roleRepository.save(pharmacistRole);
        }

        // Tự động gán quyền cho DOCTOR
        com.clinic.booking.entity.Role doctorRole = roleRepository.findByName("DOCTOR").orElse(null);
        if (doctorRole != null) {
            java.util.Set<com.clinic.booking.entity.Permission> perms = new java.util.HashSet<>();
            permissionRepository.findByName("EXAMINE_PATIENT").ifPresent(perms::add);
            permissionRepository.findByName("VIEW_MEDICAL_RECORD").ifPresent(perms::add);
            permissionRepository.findByName("VIEW_PATIENT_LIST").ifPresent(perms::add);
            doctorRole.setPermissions(perms);
            roleRepository.save(doctorRole);
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