package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
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
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private UserCardController userCardController;

    @Test
    void getMyCards_shouldReturnPagedCardsWithStatusFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        String status = "ACTIVE";
        CardDTO dto = new CardDTO(UUID.randomUUID(), "**** **** **** 1111", "Test User", null, "ACTIVE", BigDecimal.ZERO, null, null);
        Page<CardDTO> page = new PageImpl<>(Collections.singletonList(dto));

        when(cardService.getMyCards(pageable, status)).thenReturn(page);

        ResponseEntity<Page<CardDTO>> response = userCardController.getMyCards(pageable, status);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getTotalElements());
        verify(cardService).getMyCards(pageable, status);
    }

    @Test
    void getCardById_shouldReturnCardIfFound() {
        UUID id = UUID.randomUUID();
        CardDTO dto = new CardDTO(id, "**** **** **** 1111", "Test User", null, "ACTIVE", BigDecimal.ZERO, null, null);

        when(cardService.getCardById(id)).thenReturn(dto);

        ResponseEntity<CardDTO> response = userCardController.getCardById(id);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(dto, response.getBody());
        verify(cardService).getCardById(id);
    }

}
