package com.example.Kirana.dto.response;

import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private String error;
    public ErrorResponse() {
    }
}
