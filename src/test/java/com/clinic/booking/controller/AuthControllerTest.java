package com.clinic.booking.controller;

import com.clinic.booking.dto.auth.AuthenticationRequest;
import com.clinic.booking.dto.auth.AuthenticationResponse;
import com.clinic.booking.dto.auth.RegisterRequest;
import com.clinic.booking.dto.auth.ResetPasswordRequest;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.dto.user.UserResponse;
import com.clinic.booking.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@TestPropertySource("/test.properties")
@AutoConfigureMockMvc // Bỏ qua Security filters khi test Controller
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    private RegisterRequest request;
    private AuthenticationRequest authenticationRequest;
    private ResetPasswordRequest resetPasswordRequest;
    private AuthenticationResponse response;
    private LocalDate dob;

    @BeforeEach
    void initData(){
        dob = LocalDate.of(1990, 1, 1);

        request = RegisterRequest.builder()
                .fullName("NguyenSon")
                .email("son@gmail.com")
                .password("123456")
                .phone("0123456789")
                .gender("MALE")
                .dateOfBirth(dob)
                .address("HANOI")
                .build();

        authenticationRequest = AuthenticationRequest.builder()
                .email("son@gmail.com")
                .password("123456")
                .build();

        resetPasswordRequest = ResetPasswordRequest.builder()
                .email("son@gmail.com")
                .otp("123456")
                .newPassword("123456")
                .build();

        response = AuthenticationResponse.builder()
                .token("asdfghjk")
                .roles(List.of("ROLE_USER", "ROLE_ADMIN"))
                .build();
    }

    @Test
    void register_validRequest_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        Mockito.when(authenticationService.register(ArgumentMatchers.any()))
                .thenReturn(response);

        //WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())

                // Kiểm tra code của ApiResponse
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))

                // Kiểm tra token bên trong object result
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.token").value("asdfghjk"))

                // Kiểm tra mảng roles bên trong object result để khớp hoàn toàn data mock
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.roles.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.roles[0]").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.roles[1]").value("ROLE_ADMIN"));

    }

    @Test
    void login_validRequest_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        Mockito.when(authenticationService.authenticate(ArgumentMatchers.any()))
                .thenReturn(response);

        //WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())

                // Kiểm tra code của ApiResponse
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))

                // Kiểm tra token bên trong object result
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.token").value("asdfghjk"))

                // Kiểm tra mảng roles bên trong object result để khớp hoàn toàn data mock
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.roles.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.roles[0]").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.roles[1]").value("ROLE_ADMIN"));

    }

    @Test
    void forgotPassword_validEmail_success() throws Exception {
        // GIVEN: Chuẩn bị dữ liệu đầu vào
        String email = "son@gmail.com";

        // Giả lập hành vi của Service: Không làm gì cả (doNothing) khi hàm forgotPassword được gọi
        Mockito.doNothing().when(authenticationService).forgotPassword(ArgumentMatchers.anyString());

        // WHEN & THEN: Gọi API và kiểm chứng kết quả
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/forgot-password")
                        .param("email", email) // <-- Truyền RequestParam ở đây
                        .contentType(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("Mã OTP đã được gửi đến email của bạn."));
    }


    @Test
    void resetPassword_validRequest_success() throws Exception {
        // GIVEN: Chuẩn bị dữ liệu đầu vào

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(resetPasswordRequest);

        Mockito.doNothing().when(authenticationService).resetPassword(ArgumentMatchers.any(ResetPasswordRequest.class));

        // WHEN & THEN: Gọi API và kiểm chứng kết quả
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))

                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("Khôi phục mật khẩu thành công!"));
    }
}
