package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.service.CardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminCardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private AdminCardController adminCardController;

    @Test
    void createCard_shouldCallServiceAndReturn201() {
        UUID userId = UUID.randomUUID();
        CreateCardRequest request = new CreateCardRequest("4111111111111111", "Test User", "12/27");
        CardDTO createdCard = new CardDTO(UUID.randomUUID(), "**** **** **** 1111", "Test User", null, "ACTIVE", BigDecimal.ZERO, null, null);

        when(cardService.createCardForUser(userId, request)).thenReturn(createdCard);

        ResponseEntity<CardDTO> response = adminCardController.createCard(userId, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdCard, response.getBody());
        verify(cardService).createCardForUser(userId, request);
    }

    @Test
    void blockCard_shouldCallServiceAndReturn204() {
        UUID cardId = UUID.randomUUID();

        ResponseEntity<Void> response = adminCardController.blockCard(cardId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cardService).blockCard(cardId);
    }

    @Test
    void unblockCard_shouldCallServiceAndReturn204() {
        UUID cardId = UUID.randomUUID();

        ResponseEntity<Void> response = adminCardController.unblockCard(cardId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cardService).unblockCard(cardId);
    }

    @Test
    void getAllCards_shouldReturnPagedCards() {
        Pageable pageable = PageRequest.of(0, 10);
        CardDTO dto = new CardDTO(UUID.randomUUID(), "**** **** **** 1111", "Test", null, "ACTIVE", BigDecimal.ZERO, null, null);
        Page<CardDTO> page = new PageImpl<>(Collections.singletonList(dto));

        when(cardService.getAllCards(pageable)).thenReturn(page);

        ResponseEntity<Page<CardDTO>> response = adminCardController.getAllCards(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        verify(cardService).getAllCards(pageable);
    }

}
