package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class UserCardController {

    private final CardService cardService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get current user's cards", description = "Returns paginated list of cards belonging to the authenticated user with optional status filter. Requires JWT in cookie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not have USER role")
    })
    public ResponseEntity<Page<CardDTO>> getMyCards(
            Pageable pageable,
            @RequestParam(required = false) String status) {  
        Page<CardDTO> cards = cardService.getMyCards(pageable, status);
        return ResponseEntity.ok(cards);
    }

    // Детали карты — тоже только свои
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get card by ID", description = "Returns details of a specific card if it belongs to the user. Requires JWT in cookie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card found"),
            @ApiResponse(responseCode = "404", description = "Card not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<CardDTO> getCardById(@PathVariable UUID id) {
        CardDTO card = cardService.getCardById(id);
        return ResponseEntity.ok(card);
    }

}
