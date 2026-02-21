package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
public class AdminCardController {

    private final CardService cardService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new card for a user", description = "Creates a new card for any user by admin. Requires JWT in cookie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid card data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not ADMIN role")
    })
    public ResponseEntity<CardDTO> createCard(
            @RequestParam UUID userId,  // ← передаём id юзера как параметр
            @Valid @RequestBody CreateCardRequest request) {
        CardDTO created = cardService.createCardForUser(userId, request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Block a card", description = "Blocks any card by ID. Requires JWT in cookie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card blocked"),
            @ApiResponse(responseCode = "404", description = "Card not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not ADMIN role")
    })
    public ResponseEntity<Void> blockCard(@PathVariable UUID id) {
        cardService.blockCard(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Unblock a card", description = "Unblocks any card by ID. Requires JWT in cookie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card unblocked"),
            @ApiResponse(responseCode = "404", description = "Card not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not ADMIN role")
    })
    public ResponseEntity<Void> unblockCard(@PathVariable UUID id) {
        cardService.unblockCard(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all cards", description = "Returns paginated list of all cards in the system. Requires JWT in cookie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not have ADMIN role")
    })
    public ResponseEntity<Page<CardDTO>> getAllCards(Pageable pageable) {
        Page<CardDTO> cards = cardService.getAllCards(pageable);
        return ResponseEntity.ok(cards);
    }

}
