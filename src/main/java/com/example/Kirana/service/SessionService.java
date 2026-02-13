package com.example.Kirana.service;

import com.example.Kirana.config.JwtUtil;
import com.github.f4b6a3.ulid.UlidCreator;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
@Service
public class SessionService {
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redis;

    public SessionService(JwtUtil jwtUtil, StringRedisTemplate redis) {
        this.jwtUtil = jwtUtil;
        this.redis = redis;
    }

    public String createSession(String userId, String role, String kId) {

        String accessToken =
                jwtUtil.generateAccessToken(userId, role, kId);

        String sessionId = UlidCreator.getUlid().toString();

        redis.opsForValue().set(
                "session:" + sessionId,
                accessToken,
                Duration.ofMinutes(15)
        );

        return jwtUtil.generateSessionToken(sessionId);
    }
}
