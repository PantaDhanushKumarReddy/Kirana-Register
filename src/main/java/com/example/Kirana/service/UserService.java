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

@Service
public class UserService {
    private UserDao userDao;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private SessionService sessionService;
    public UserService(UserDao userDao, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,SessionService sessionService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.sessionService=sessionService;
    }

    public User create(UserRequestDto userRequestDto) {
        User user = new User();
        user.setId(UlidCreator.getUlid().toString());
        user.setKId(userRequestDto.getKId());
        user.setEmail(userRequestDto.getEmail());
        user.setRole(userRequestDto.getRole());
        user.setActive(true);
        user.setCreatedAt(Instant.now());

        if (userRequestDto.getRole()== Role.CUSTOMER) {
            user.setPassword(null);  // No password for customer
        } else {
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        }
        return  userDao.save(user);
    }

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
                user.getKId()
        );
        return new AuthResponseDto(token);
    }
}
