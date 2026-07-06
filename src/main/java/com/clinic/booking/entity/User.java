package com.clinic.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    // ... (Giữ nguyên các trường thuộc tính từ id đến status như bạn đã viết) ...

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String phone;
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String address;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    private String status;

    // --- Các hàm bắt buộc phải Override của interface UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        if (roles != null) {
            for (Role r : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + r.getName().toUpperCase()));
                if (r.getPermissions() != null) {
                    for (Permission p : r.getPermissions()) {
                        authorities.add(new SimpleGrantedAuthority(p.getName().toUpperCase()));
                    }
                }
            }
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return email; // Trong hệ thống này, email chính là username dùng để đăng nhập
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return "ACTIVE".equalsIgnoreCase(status); // Nếu status bị Inactive thì khóa tài khoản
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}