package com.example.Kirana.service;

import com.example.Kirana.config.JwtUtil;
import com.github.f4b6a3.ulid.UlidCreator;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
/**
 * SessionService
 *
 * Manages user login sessions using Redis and JWT.
 * Implements a two-token approach:
 *  - Internal access token (stored in Redis, never exposed to client)
 *  - Session token (returned to client, contains only session ID)
 */
@Service
public class SessionService {
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redis;
    /**
     * Constructor-based dependency injection.
     *
     * @param jwtUtil JWT utility
     * @param redis Redis template
     */
    public SessionService(JwtUtil jwtUtil, StringRedisTemplate redis) {
        this.jwtUtil = jwtUtil;
        this.redis = redis;
    }
    /**
     * Creates a new user session.
     *
     * Flow:
     *  1. Generate an internal access token containing userId, role, and kiranaId
     *  2. Generate a unique session ID (ULID)
     *  3. Store the access token in Redis mapped to the session ID
     *  4. Return a session token (JWT) containing only the session ID
     *
     * Security benefit:
     *  - Access token is never exposed to the client which contains of kirana Store Id
     *  - Session can be invalidated instantly by deleting Redis entry
     *
     * @param userId User ID
     * @param role User role
     * @param kiranaId Kirana ID
     * @return Session JWT token to be sent to the client
     */
    public String createSession(String userId, String role, String kiranaId) {

        String accessToken =
                jwtUtil.generateAccessToken(userId, role, kiranaId);
        String sessionId = UlidCreator.getUlid().toString();

        redis.opsForValue().set(
                "session:" + sessionId,
                accessToken,
                Duration.ofMinutes(15)
        );

        return jwtUtil.generateSessionToken(sessionId);
    }
}
