package com.example.Kirana.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
@Component
public class JwtUtil {

    private static final String ACCESS_SECRET =
            "access-secret-key-access-secret-key-256bit";

    private static final String SESSION_SECRET =
            "session-secret-key-session-secret-key-256bit";

    private final Key accessKey =
            Keys.hmacShaKeyFor(ACCESS_SECRET.getBytes(StandardCharsets.UTF_8));

    private final Key sessionKey =
            Keys.hmacShaKeyFor(SESSION_SECRET.getBytes(StandardCharsets.UTF_8));

    private final long EXP = 1000 * 60 * 15;

    public String generateAccessToken(String userId, String role, String kiranaId) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .claim("kiranaId", kiranaId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXP))
                .signWith(accessKey)
                .compact();
    }

    public String generateSessionToken(String sessionId) {
        return Jwts.builder()
                .claim("sessionId", sessionId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXP))
                .signWith(sessionKey)
                .compact();
    }
    public Claims validateAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public Claims validateSessionToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(sessionKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
