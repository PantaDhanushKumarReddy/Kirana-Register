package com.example.Kirana.controller;

import com.example.Kirana.RateLimiterSlidingWindow.SlidingWindowRateLimiterService;
import com.example.Kirana.dto.request.LoginRequestDto;
import com.example.Kirana.dto.response.ApiResponse;
import com.example.Kirana.dto.response.AuthResponseDto;
import com.example.Kirana.dto.response.ErrorResponse;
import com.example.Kirana.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private SlidingWindowRateLimiterService slidingWindowRateLimiterService;
    private static final Logger log =
            LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService, SlidingWindowRateLimiterService slidingWindowRateLimiterService) {
        this.userService = userService;
        this.slidingWindowRateLimiterService = slidingWindowRateLimiterService;
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
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequestDto dto) {

        if(slidingWindowRateLimiterService.isAllowed(dto.getEmail())){
            long remainingAttempts =
                    slidingWindowRateLimiterService.getRemainingAttempts(dto.getEmail());

            log.info(
                    "Login attempt for email={}, remainingAttempts={}",
                    dto.getEmail(),
                    remainingAttempts
            );
            return ResponseEntity.ok(userService.login(dto));
        }else{
            ErrorResponse response=new ErrorResponse();
            response.setError("RATE_LIMITED");
            response.setMessage("Too many login attempts. Please try again later.");
            return ResponseEntity.status(429) // too many request
                    .header("X-Retry-After", "60")
                    .body(response);
        }
    }
}
