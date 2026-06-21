package com.clinic.booking.controller;

import com.clinic.booking.dto.SpecialtyDTO;
import com.clinic.booking.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
@CrossOrigin(origins = "*") // Tạm thời cho phép mọi domain gọi API để test với React
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @GetMapping
    public ResponseEntity<List<SpecialtyDTO>> getAll() {
        return ResponseEntity.ok(specialtyService.getAllSpecialties());
    }

//    @PostMapping
//    public ResponseEntity<SpecialtyDTO> create(@RequestBody SpecialtyDTO dto) {
//        return ResponseEntity.ok(specialtyService.createSpecialty(dto));
//    }
}