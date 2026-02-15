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
    /**
     * Fetch a Kirana store by ID.
     *
     * Endpoint: GET /api/kirana/{id}
     *
     * @param id Kirana ID
     * @return Kirana details
     */

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Kirana> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Register a new Kirana store.
     *
     * Endpoint: POST /api/kirana/register
     *
     * @param dto Kirana registration request data
     * @return Created Kirana entity
     */
    @PostMapping("register")
    public ResponseEntity<Kirana> create(@Valid @RequestBody KiranaRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    /**
     * Deactivate a Kirana store (soft delete).
     *
     * Endpoint: PATCH /api/kirana/{id}/deactivate
     *
     * @param id Kirana ID
     * @return Success response message
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse> deactivate(@PathVariable String id) {
        service.deactivate(id);
        return ResponseEntity.ok(
                new ApiResponse("Kirana store "+id+" is deactivated successfully")
        );
    }
}
