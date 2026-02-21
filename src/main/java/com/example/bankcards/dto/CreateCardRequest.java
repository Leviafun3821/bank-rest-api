package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCardRequest(
        @NotBlank @Size(min = 16, max = 16, message = "Card number must be 16 digits")
        String cardNumber,

        @NotBlank
        String ownerName,

        @NotBlank
        String expiryDate // формат MM/YY
) { }
