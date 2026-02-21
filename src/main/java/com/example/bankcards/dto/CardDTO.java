package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CardDTO(
        UUID id,
        String maskedNumber,
        String ownerName,
        LocalDate expiryDate,
        String status,
        BigDecimal balance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }
