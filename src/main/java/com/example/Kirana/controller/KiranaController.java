package com.example.Kirana.controller;

import com.example.Kirana.dto.request.KiranaRequestDto;
import com.example.Kirana.dto.response.ApiResponse;
import com.example.Kirana.entity.mongo.Kirana;
import com.example.Kirana.service.KiranaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kirana")
@Validated
public class KiranaController {
    public KiranaController(KiranaService service) {
        this.service = service;
    }

    private KiranaService service;
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Kirana> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("register")
    public ResponseEntity<Kirana> create(@Valid @RequestBody KiranaRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse> deactivate(@PathVariable String id) {
        service.deactivate(id);
        return ResponseEntity.ok(
                new ApiResponse("Kirana store "+id+" is deactivated successfully")
        );
    }
}
