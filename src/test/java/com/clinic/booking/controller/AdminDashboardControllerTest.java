package com.clinic.booking.controller;

import com.clinic.booking.dto.appointment.AppointmentResponse;
import com.clinic.booking.service.AdminDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("/test.properties")
@AutoConfigureMockMvc(addFilters = false)
public class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminDashboardService adminDashboardService;

    private AppointmentResponse appointmentResponse;
    private Map<String, Object> statsResponse;

    @BeforeEach
    void initData() {
        appointmentResponse = AppointmentResponse.builder()
                .id(1L)
                .doctorId(2L)
                .scheduleId(3L)
                .doctorName("Dr. John")
                .patientName("Nguyen Son")
                .timeSlot("08:00 - 09:00")
                .status("PENDING")
                .build();

        statsResponse = new HashMap<>();
        statsResponse.put("totalAppointments", 10);
        statsResponse.put("totalRevenue", 1000000.0);
    }

    @Test
    void getDashboardStats_success() throws Exception {
        // GIVEN
        when(adminDashboardService.getDashboardStats()).thenReturn(statsResponse);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.totalAppointments").value(10));
    }

    @Test
    void getAppointments_success() throws Exception {
        // GIVEN
        Page<AppointmentResponse> pageResponse = new PageImpl<>(List.of(appointmentResponse));
        // Note: isNull() instead of anyString() for null search/status parameters
        when(adminDashboardService.getAppointments(anyInt(), anyInt(), isNull(), isNull())).thenReturn(pageResponse);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/all-appointments")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.content[0].id").value(1));
    }

    @Test
    void getAllAppointmentsList_success() throws Exception {
        // GIVEN
        when(adminDashboardService.getAllAppointmentsList()).thenReturn(List.of(appointmentResponse));

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/all-appointments/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].id").value(1));
    }

    @Test
    void updateAppointmentStatus_success() throws Exception {
        // GIVEN
        doNothing().when(adminDashboardService).updateAppointmentStatus(eq(1L), anyString());

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/appointments/1/status")
                .param("status", "COMPLETED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("Cập nhật trạng thái thành công"));
    }
}
