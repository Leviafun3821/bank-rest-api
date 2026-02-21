package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private int status;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private Map<String, String> errors;  // для валидации — может быть null

    public ErrorResponse(int status, String message, LocalDateTime timestamp, String path) {
        this(status, message, timestamp, path, null);
    }

}
