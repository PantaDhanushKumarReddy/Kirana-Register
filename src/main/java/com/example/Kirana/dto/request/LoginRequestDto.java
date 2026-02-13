package com.example.Kirana.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
/**
 * LoginRequestDto
 *
 * Request DTO used for user authentication.
 * Contains credentials required for login.
 */
@Data
public class LoginRequestDto {
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE)
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
