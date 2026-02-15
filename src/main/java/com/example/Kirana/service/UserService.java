package com.example.Kirana.service;

import com.example.Kirana.config.JwtUtil;
import com.example.Kirana.dao.mongo.UserDao;
import com.example.Kirana.dto.request.LoginRequestDto;
import com.example.Kirana.dto.request.UserRequestDto;
import com.example.Kirana.dto.response.AuthResponseDto;
import com.example.Kirana.entity.mongo.User;
import com.example.Kirana.enums.Role;
import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
/**
 * UserService
 *
 * Handles user management and authentication logic.
 * Responsible for:
 *  - Creating users
 *  - Validating login credentials
 *  - Issuing authentication tokens via session management service
 */
@Service
public class UserService {
    private UserDao userDao;
    private PasswordEncoder passwordEncoder;
    private SessionService sessionService;
    /**
     * Constructor-based dependency injection.
     *
     * @param userDao User DAO
     * @param passwordEncoder Password encoder
     * @param sessionService Session service
     */
    public UserService(UserDao userDao, PasswordEncoder passwordEncoder,SessionService sessionService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.sessionService=sessionService;
    }
    /**
     * Creates a new user.
     *
     * Business rules:
     *  - CUSTOMER users do not have passwords
     *  - Non-customer users must have an encrypted password
     *  - User is created in active state by default
     *
     * @param userRequestDto User creation request data
     * @return Persisted User entity
     */
    public User create(UserRequestDto userRequestDto) {
        User user = new User();
        user.setKiranaId(userRequestDto.getKiranaId());
        user.setEmail(userRequestDto.getEmail());
        user.setRole(userRequestDto.getRole());
        user.setActive(true);

        if (userRequestDto.getRole()== Role.CUSTOMER) {
            user.setPassword(null);  // No password for customer
        } else {
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        }
        return  userDao.save(user);
    }
    /**
     * Authenticates a user and creates a session.
     *
     * Flow:
     *  1. Fetch active user by email
     *  2. Reject CUSTOMER logins
     *  3. Validate password
     *  4. Create sessionID using redis and issue token and original access token
     *   will be stored in redis
     * @param dto Login request containing credentials
     * @return Authentication response containing access token
     */
    public AuthResponseDto login(@Valid LoginRequestDto dto) {
        User user=userDao.findByEmail(dto.getEmail());
        if (user.getRole() == Role.CUSTOMER) {
            throw new RuntimeException("Customer login not allowed");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token =sessionService.createSession(
                user.getId(),
                user.getRole().name(),
                user.getKiranaId()
        );
        return new AuthResponseDto(token);
    }
}
