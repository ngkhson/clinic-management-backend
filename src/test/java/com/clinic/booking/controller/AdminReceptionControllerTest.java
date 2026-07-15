package com.clinic.booking.controller;

import com.clinic.booking.dto.reception.AdminReceptionRequest;
import com.clinic.booking.service.AdminReceptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("/test.properties")
@AutoConfigureMockMvc(addFilters = false)
public class AdminReceptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminReceptionService receptionService;

    @Autowired
    private ObjectMapper objectMapper;

    private AdminReceptionRequest request;

    @BeforeEach
    void initData() {
        request = new AdminReceptionRequest();
        request.setPatientId(1L);
        request.setDoctorId(2L);
        request.setScheduleId(3L);
        request.setSymptoms("Ho, sốt");
        request.setTemperature(38.5);
        request.setBloodPressure("120/80");
        request.setPulse(80);
        request.setWeight(60.0);
        request.setHeight(170.0);
    }

    @Test
    void createReception_success() throws Exception {
        // GIVEN
        when(receptionService.createReceptionAndVitals(any())).thenReturn(null);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/reception")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000));
    }
}
