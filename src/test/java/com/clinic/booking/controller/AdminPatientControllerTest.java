package com.clinic.booking.controller;

import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.dto.doctor.AdminDoctorResponse;
import com.clinic.booking.dto.doctor.DoctorCreationRequest;
import com.clinic.booking.dto.user.UserResponse;
import com.clinic.booking.service.AdminPatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("/test.properties")
@AutoConfigureMockMvc(addFilters = false)
public class AdminPatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminPatientService adminPatientService;

    private UserResponse userResponse;
//    private DoctorCreationRequest doctorCreationRequest;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void initData() {
        userResponse = UserResponse.builder()
                .id(1L)
                .fullName("ngkhson")
                .email("son@gmail.com")
                .phone("0123456789")
                .gender("MALE")
                .dateOfBirth(LocalDate.of(2005, 11, 25))
                .address("hanoi")
                .status("ACTIVE")
                .build();

    }

    @Test
    void getAllPatients_success() throws Exception {
        // GIVEN
        Page<UserResponse> pageResponse = new PageImpl<>(List.of(userResponse));
        when(adminPatientService.getAllPatients(anyInt(), anyInt(), isNull())).thenReturn(pageResponse);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/patients")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.content[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.content[0].fullName").value("ngkhson"));
    }

    @Test
    void getDoctors_success() throws Exception {
        // GIVEN
        when(adminPatientService.getPatients()).thenReturn(List.of(userResponse));

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/patients/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].id").value(1));
    }

    @Test
    void updatePatientStatus_success() throws Exception {
        // GIVEN
        doNothing().when(adminPatientService).togglePatientStatus(1L);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/patients/1/toggle-status")
                        .param("status", "COMPLETED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("Cập nhật trạng thái thành công!"));
    }

}
