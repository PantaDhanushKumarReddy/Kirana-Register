package com.example.Kirana.controller;

import com.example.Kirana.dao.mongo.KiranaDao;
import com.example.Kirana.dto.request.UserRequestDto;
import com.example.Kirana.entity.mongo.Kirana;
import com.example.Kirana.entity.mongo.User;
import com.example.Kirana.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * UserController
 *
 * Exposes APIs for user management.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final KiranaDao kiranaDao;
    /**
     * Constructor-based dependency injection for UserService.
     *
     * @param service User service
     */
    public UserController(UserService service, KiranaDao kiranaDao) {
        this.kiranaDao = kiranaDao;
        this.service = service;
    }
    /**
     * Register a new user.
     *
     * Endpoint: POST /api/users/register
     *
     * @param dto User registration request data
     * @return Created User entity
     */
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<User> register(
            @Valid @RequestBody UserRequestDto dto) {
        Kirana kirana=kiranaDao.findActiveById(dto.getKiranaId());
        String LoggedUserRole=getUserRole();
        return ResponseEntity.ok(service.create(LoggedUserRole,dto));
    }

    private String getUserRole() {
        Claims claims = (Claims) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return claims.get("role", String.class);
    }
}
