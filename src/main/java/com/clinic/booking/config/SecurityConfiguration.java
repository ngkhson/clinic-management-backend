package com.clinic.booking.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF vì chúng ta dùng JWT
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Kích hoạt CORS cho React gọi
                .authorizeHttpRequests(auth -> auth
                        // CÁC ĐƯỜNG DẪN CÔNG KHAI (Không cần đăng nhập)
                        .requestMatchers("/api/auth/**").permitAll() // Đăng ký, đăng nhập
                        .requestMatchers("/api/test").permitAll() // Test API
                        // MỞ TẠM THỜI ĐỂ BẠN DỄ TEST (Sau này sẽ khóa lại)
                        .requestMatchers("/api/specialties/**").permitAll()
                        .requestMatchers("/api/doctors/**").permitAll()
//                        .requestMatchers("/api/schedules/**").permitAll()
//                        .requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/admin/doctors").permitAll()
                        .requestMatchers("/api/admin/doctors").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reviews").authenticated() // Cho phép user đã đăng nhập được gửi đánh giá
                        .requestMatchers("/api/reviews/doctor/**").permitAll()

                        // CÁC ĐƯỜNG DẪN CẦN PHÂN QUYỀN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/doctor/**").hasRole("DOCTOR")

                        // Các đường dẫn còn lại bắt buộc phải có Token
                        .anyRequest().authenticated()
                )
                // Cấu hình không lưu session (Stateless) vì đã dùng JWT
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                // Gắn cái Filter kiểm tra Token vào trước quá trình xử lý xác thực mặc định
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Cấu hình CORS để Front-end Vite React (localhost:5173) có thể gọi API mà không bị lỗi
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // URL của React
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}