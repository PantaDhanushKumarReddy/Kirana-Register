package com.example.Kirana.config;

import com.example.Kirana.dto.response.ApiResponse;
import com.example.Kirana.dto.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redis;

    public JwtFilter(JwtUtil jwtUtil, StringRedisTemplate redis) {
        this.jwtUtil = jwtUtil;
        this.redis = redis;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String sessionJwt = header.substring(7);
            Claims sessionClaims =
                    jwtUtil.validateSessionToken(sessionJwt);

            String sessionId =
                    sessionClaims.get("sessionId", String.class);

            String accessToken =
                    redis.opsForValue().get("session:" + sessionId);

            Claims claims =
                    jwtUtil.validateAccessToken(accessToken);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            claims.getSubject(),
                            null,
                            List.of(() -> "ROLE_" + claims.get("role"))
                    );

            auth.setDetails(claims);
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
//            response.getWriter().write("""
//            {
//              "error": "INVALID_TOKEN",
//              "message": "JWT token is invalid or expired"
//            }
//            """);
            ErrorResponse apiResponse=new ErrorResponse();
            apiResponse.setError("INVALID_TOKEN");
            apiResponse.setMessage("JWT token is invalid or expired");

            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(apiResponse));
            response.setStatus(401);
            return;
        }

        chain.doFilter(request, response);
    }

}
