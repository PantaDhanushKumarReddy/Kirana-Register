package com.example.Kirana.controller;

import com.example.Kirana.dto.request.LoginRequestDto;
import com.example.Kirana.dto.response.AuthResponseDto;
import com.example.Kirana.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * AuthController
 *
 * Handles authentication-related APIs such as user login.
 * Delegates all business logic to the UserService.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private UserService userService;
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    /**
     * Login API
     *
     * Authenticates a user using email and password.
     *
     * Endpoint: POST /api/auth/login
     *
     * @param dto Login request containing user credentials
     * @return AuthResponseDto containing tokens
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto dto) {

        return ResponseEntity.ok(userService.login(dto));
    }
}
