package com.example.Kirana.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * ApiResponse
 *
 * Generic response DTO used for returning simple success or status messages
 * from API endpoints.
 */
@Data
@AllArgsConstructor
public class ApiResponse {
    private String message;
}
