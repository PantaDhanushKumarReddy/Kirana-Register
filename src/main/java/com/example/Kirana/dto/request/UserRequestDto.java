package com.example.Kirana.dto.request;

import com.example.Kirana.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
/**
 * UserRequestDto
 *
 * Request DTO used for creating r.
 * Used by ADMIN APIs for user management.
 */
@Data
public class UserRequestDto {
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE)
    private String email;

    private String password;

    @NotNull
    private Role role;

    private String kiranaId;
}
