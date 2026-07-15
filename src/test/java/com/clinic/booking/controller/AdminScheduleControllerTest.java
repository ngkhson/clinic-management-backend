package com.clinic.booking.controller;

import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.dto.doctor.DoctorCreationRequest;
import com.clinic.booking.dto.schedule.ScheduleGenerateRequest;
import com.clinic.booking.dto.schedule.ScheduleSlotRequest;
import com.clinic.booking.dto.schedule.ScheduleUpdateRequest;
import com.clinic.booking.service.AdminScheduleService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("/test.properties")
@AutoConfigureMockMvc(addFilters = false)
public class AdminScheduleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminScheduleService adminScheduleService;

    @Autowired
    private ObjectMapper objectMapper;

    private ScheduleGenerateRequest generateRequest;
    private ScheduleSlotRequest slot1, slot2;
    private ScheduleUpdateRequest updateRequest;

    @BeforeEach
    void initData(){
        slot1 = ScheduleSlotRequest.builder()
                .timeSlot("08:30") // Khung giờ 08:30
                .maxPatients(5)            // Tối đa 5 bệnh nhân
                .build();

        slot2 = ScheduleSlotRequest.builder()
                .timeSlot("09:00")  // Khung giờ 09:00
                .maxPatients(5)
                .build();

        generateRequest = ScheduleGenerateRequest.builder()
                .doctorId(1L)
                .date(LocalDate.of(2026, 7, 15))
                .slots(List.of(slot1, slot2))
                .build();

        updateRequest = ScheduleUpdateRequest.builder()
                .maxPatients(3)
                .build();
    }

    @Test
    void generateSchedule_success() throws Exception {
        // GIVEN
        when(adminScheduleService.generateSchedules(any(ScheduleGenerateRequest.class))).thenReturn(2);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/schedules/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(generateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("Đã tạo thành công 2 ca khám!"));
    }

    @Test
    void updateSchedule_success() throws Exception {
        // GIVEN
        doNothing().when(adminScheduleService).updateSchedule(eq(1L), any(ScheduleUpdateRequest.class));

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/schedules/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("Cập nhật ca khám thành công"));
    }

    @Test
    void deleteSchedule_success() throws Exception {
        // GIVEN
        doNothing().when(adminScheduleService).deleteSchedule(1L);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/schedules/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("Xoá ca khám thành công"));
    }
}
