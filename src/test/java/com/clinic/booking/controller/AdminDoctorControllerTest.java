package com.clinic.booking.controller;

import com.clinic.booking.dto.doctor.AdminDoctorResponse;
import com.clinic.booking.dto.doctor.DoctorCreationRequest;
import com.clinic.booking.service.AdminDoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;


import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("/test.properties")
@AutoConfigureMockMvc(addFilters = false)
public class AdminDoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminDoctorService adminDoctorService;

    private AdminDoctorResponse adminDoctorResponse;
    private DoctorCreationRequest doctorCreationRequest;
    
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void initData() {

        adminDoctorResponse = AdminDoctorResponse.builder()
                .id(1L)
                .userId(2L)
                .fullName("Dr. Smith")
                .degree("PhD")
                .biography("Cardiologist with 10 years experience")
                .specialtyId(1L)
                .specialtyName("Cardiology")
                .email("dr.smith@example.com")
                .phone("0123456789")
                .gender("MALE")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .address("123 Clinic St")
                .status("ACTIVE")
                .build();

        doctorCreationRequest = DoctorCreationRequest.builder()
                .email("dr.new@example.com")
                .password("Password123")
                .fullName("Dr. New")
                .address("456 New St")
                .gender("FEMALE")
                .dateOfBirth(LocalDate.of(1985, 5, 5))
                .phone("0987654321")
                .specialtyId(2L)
                .degree("MD")
                .biography("New Neurologist")
                .build();
    }

    @Test
    void getAllDoctors_success() throws Exception {
        // GIVEN
        Page<AdminDoctorResponse> pageResponse = new PageImpl<>(List.of(adminDoctorResponse));
        when(adminDoctorService.getAllDoctors(anyInt(), anyInt(), isNull())).thenReturn(pageResponse);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/doctors")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.content[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.content[0].fullName").value("Dr. Smith"));
    }

    @Test
    void getDoctors_success() throws Exception {
        // GIVEN
        when(adminDoctorService.getDoctors()).thenReturn(List.of(adminDoctorResponse));

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/doctors/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].id").value(1));
    }

    @Test
    void createDoctor_success() throws Exception {
        // GIVEN
        when(adminDoctorService.createDoctor(any(DoctorCreationRequest.class))).thenReturn(adminDoctorResponse);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorCreationRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.id").value(1));
    }

    @Test
    void createDoctor_exception_throws400() throws Exception {
        // GIVEN
        when(adminDoctorService.createDoctor(any(DoctorCreationRequest.class)))
                .thenThrow(new RuntimeException("Email đã tồn tại"));

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorCreationRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk()) // Controller trả về ApiResponse chứa mã 400 bên trong
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Email đã tồn tại"));
    }

    @Test
    void updateDoctor_success() throws Exception {
        // GIVEN
        when(adminDoctorService.updateDoctor(eq(1L), any(DoctorCreationRequest.class))).thenReturn(adminDoctorResponse);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/doctors/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorCreationRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.id").value(1));
    }

    @Test
    void deleteDoctor_success() throws Exception {
        // GIVEN
        doNothing().when(adminDoctorService).deleteDoctor(1L);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/doctors/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("Xóa thành công!"));
    }
}
