package com.example.bankcards.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(min = 3, max = 50) String username,
        @Email String email,
        String role  // ROLE_USER или ROLE_ADMIN
) { }
