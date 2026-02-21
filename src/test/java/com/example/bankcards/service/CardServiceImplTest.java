package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.CardServiceImpl;
import com.example.bankcards.util.CardEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private CardEncryptor cardEncryptor;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    void createCardForUser_shouldEncryptAndMaskCardNumber() {
        UUID userId = UUID.randomUUID();
        CreateCardRequest request = new CreateCardRequest("4111111111111111", "Test User", "12/27");
        User user = new User();
        Card card = new Card();
        Card savedCard = new Card();
        CardDTO dto = new CardDTO(UUID.randomUUID(), "**** **** **** 1111", "Test User", null, "ACTIVE", BigDecimal.ZERO, null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardMapper.toEntity(request)).thenReturn(card);
        when(cardEncryptor.encrypt("4111111111111111")).thenReturn("encryptedBase64");
        when(cardMapper.maskCardNumber("4111111111111111")).thenReturn("**** **** **** 1111");
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);
        when(cardMapper.toDTO(savedCard)).thenReturn(dto);

        CardDTO result = cardService.createCardForUser(userId, request);

        verify(cardEncryptor).encrypt("4111111111111111");
        verify(cardMapper).maskCardNumber("4111111111111111");
        verify(cardRepository).save(card);
        assertEquals("**** **** **** 1111", result.maskedNumber());
        assertEquals(BigDecimal.ZERO, result.balance());
    }

    @Test
    void getAllCards_shouldReturnPagedCards() {
        Pageable pageable = PageRequest.of(0, 10);
        Card card = new Card();
        Page<Card> page = new PageImpl<>(List.of(card));
        CardDTO dto = new CardDTO(UUID.randomUUID(), "**** **** **** 1111", "Test", null, "ACTIVE", BigDecimal.ZERO, null, null);

        when(cardRepository.findAll(pageable)).thenReturn(page);
        when(cardMapper.toDTO(card)).thenReturn(dto);

        Page<CardDTO> result = cardService.getAllCards(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("**** **** **** 1111", result.getContent().get(0).maskedNumber());
        verify(cardRepository).findAll(pageable);
    }

    @Test
    void blockCard_shouldChangeStatusToBlocked() {
        UUID id = UUID.randomUUID();
        Card card = new Card();

        when(cardRepository.findById(id)).thenReturn(Optional.of(card));

        cardService.blockCard(id);

        assertEquals(Card.STATUS_BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void unblockCard_shouldChangeStatusToActive() {
        UUID id = UUID.randomUUID();
        Card card = new Card();

        when(cardRepository.findById(id)).thenReturn(Optional.of(card));

        cardService.unblockCard(id);

        assertEquals(Card.STATUS_ACTIVE, card.getStatus());
        verify(cardRepository).save(card);
    }

}
