package com.example.Kirana.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * AuthResponseDto
 *
 * Response DTO returned after successful authentication.
 * Contains the access token by using the sequence required for authorized API access.
 */
@Data
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
}
