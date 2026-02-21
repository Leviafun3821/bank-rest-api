package com.example.bankcards.dto;

public record UpdateCardRequest(
        String status // ACTIVE, BLOCKED и т.д.
) { }
