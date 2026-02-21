package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferResponse(
        UUID id,
        UUID fromCardId,
        UUID toCardId,
        BigDecimal amount,
        LocalDateTime timestamp,
        String status
) { }
