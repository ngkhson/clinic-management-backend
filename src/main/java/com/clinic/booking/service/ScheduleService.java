package com.clinic.booking.service;

import com.clinic.booking.dto.ScheduleDTO;
import com.clinic.booking.entity.Schedule;
import com.clinic.booking.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    // Lấy danh sách khung giờ của bác sĩ trong 1 ngày cụ thể
    public List<ScheduleDTO> getSchedulesByDoctorAndDate(Long doctorId, LocalDate workDate) {
        List<Schedule> schedules = scheduleRepository.findByDoctorIdAndWorkDate(doctorId, workDate);

        return schedules.stream().map(schedule -> ScheduleDTO.builder()
                .id(schedule.getId())
                .doctorId(schedule.getDoctor().getId())
                .workDate(schedule.getWorkDate())
                .timeSlot(schedule.getTimeSlot())
                .maxPatients(schedule.getMaxPatients())
                .currentPatients(schedule.getCurrentPatients())
                // Còn trống nếu số bệnh nhân hiện tại < số bệnh nhân tối đa
                .isAvailable(schedule.getCurrentPatients() < schedule.getMaxPatients())
                .build()).collect(Collectors.toList());
    }
}