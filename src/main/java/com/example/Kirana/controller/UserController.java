package com.example.Kirana.controller;

import com.example.Kirana.dto.request.UserRequestDto;
import com.example.Kirana.entity.mongo.User;
import com.example.Kirana.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }
    @PostMapping("/register")
    public ResponseEntity<User> register(
            @Valid @RequestBody UserRequestDto dto) {

        return ResponseEntity.ok(service.create(dto));
    }
}
