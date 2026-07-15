package com.clinic.booking.controller;

import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.AdminScheduleService;
import com.clinic.booking.service.SpecialtyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tools.jackson.databind.ObjectMapper;
import com.clinic.booking.dto.specialty.SpecialtyRequest;
import com.clinic.booking.dto.specialty.SpecialtyResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("/test.properties")
@AutoConfigureMockMvc(addFilters = false)
public class AdminSpecialtyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SpecialtyService specialtyService;

    @Autowired
    private ObjectMapper objectMapper;

    private SpecialtyRequest request;
    private SpecialtyResponse response;

    @BeforeEach
    void initData(){
        request = SpecialtyRequest.builder()
                .name("Nha khoa")
                .description("Chuyên khoa Răng Hàm Mặt")
                .build();

        response = SpecialtyResponse.builder()
                .id(1L)
                .name("Nha khoa")
                .description("Chuyên khoa Răng Hàm Mặt")
                .build();
    }

    @Test
    void createSpecialty_success() throws Exception {
        // GIVEN
        when(specialtyService.createSpecialty(any(SpecialtyRequest.class))).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.name").value("Nha khoa"));
    }

    @Test
    void updateSpecialty_success() throws Exception {
        //GIVEN
        when(specialtyService.updateSpecialty(eq(1L), any(SpecialtyRequest.class))).thenReturn(response);

        //WHEN THEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.name").value("Nha khoa"));
    }

    @Test
    void deleteSpeciaty_success() throws Exception {
        // GIVEN
        doNothing().when(specialtyService).deleteSpecialty(1L);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/specialties/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("Xóa thành công!"));

    }
}
