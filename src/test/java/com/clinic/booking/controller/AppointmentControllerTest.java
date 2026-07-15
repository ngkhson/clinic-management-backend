package com.clinic.booking.controller;


import com.clinic.booking.dto.appointment.AppointmentRequest;
import com.clinic.booking.dto.appointment.AppointmentResponse;
import com.clinic.booking.dto.appointment.AppointmentUpdateRequest;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.AppointmentService;
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
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("/test.properties")
@AutoConfigureMockMvc(addFilters = false)
public class AppointmentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService appointmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private AppointmentRequest appointmentRequest;
    private AppointmentUpdateRequest appointmentUpdateRequest;
    private AppointmentResponse appointmentResponse;

    @BeforeEach
    void initData(){
        appointmentRequest = AppointmentRequest.builder()
                .specialtyId(1L)
                .appointmentDate(LocalDate.of(2026, 7, 15))
                .timeSlot("08:30")
                .symptoms("Ho")
                .build();

        appointmentUpdateRequest = AppointmentUpdateRequest.builder()
                .status("PENDING")
                .symptoms("Ho")
                .build();

        appointmentResponse = AppointmentResponse.builder()
                .id(1L)
                .appointmentDate(LocalDate.of(2026, 7, 15))
                .timeSlot("08:30")
                .symptoms("Ho")
                .status("PENDING")
                .build();
    }

    @Test
    void createAppointment_success() throws Exception {
        // GIVEN
        when(appointmentService.createAppointment(any(AppointmentRequest.class))).thenReturn(appointmentResponse);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.timeSlot").value("08:30"));
    }

    @Test
    void updateAppointment_success() throws Exception {
        //GIVEN
        when(appointmentService.updateAppointment(eq(1L), any(AppointmentUpdateRequest.class))).thenReturn(appointmentResponse);

        //WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/appointments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.status").value("PENDING"));
    }

    @Test
    void cancelAppointment_success() throws Exception {
        // GIVEN
        doNothing().when(appointmentService).cancelAppointment(1L);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/appointments/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("Huỷ lịch hẹn thành công"));
    }
}
