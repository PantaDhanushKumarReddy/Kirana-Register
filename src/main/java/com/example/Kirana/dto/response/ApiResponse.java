package com.example.Kirana.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ApiResponse
 *
 * Generic response DTO used for returning simple success or status messages
 * from API endpoints.
 */
@Data
public class ApiResponse {
    private String message;

    public ApiResponse() {
    }

    public  ApiResponse(String msg){
        this.message = msg;
    }
}
