package com.example.Kirana.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
/**
 * KiranaRequestDto
 *
 * Request DTO used for registering a new Kirana (store).
 * Contains only the fields required from the client.
 */
@Data
public class KiranaRequestDto {

    @NotBlank(message = "Store name cannot be blank")
    private String kName;

    @NotBlank(message = "Location cannot be blank")
    private String location;
}
