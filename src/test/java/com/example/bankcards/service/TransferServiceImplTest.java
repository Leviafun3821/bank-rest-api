package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.service.impl.TransferServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private TransferServiceImpl transferService;

    @Test
    void transfer_successfulTransfer() {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        TransferRequest request = new TransferRequest(fromId, toId, BigDecimal.valueOf(100));

        User user = new User();
        user.setId(UUID.randomUUID());  // ← добавляем id юзеру

        Card fromCard = new Card();
        fromCard.setUser(user);
        fromCard.setBalance(BigDecimal.valueOf(500));

        Card toCard = new Card();
        toCard.setUser(user);
        toCard.setBalance(BigDecimal.ZERO);

        when(cardRepository.findById(fromId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toId)).thenReturn(Optional.of(toCard));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(i -> i.getArgument(0));

        TransferResponse response = transferService.transfer(request);

        assertEquals(BigDecimal.valueOf(400), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(100), toCard.getBalance());
        assertEquals("SUCCESS", response.status());
        verify(cardRepository, times(2)).save(any(Card.class));
        verify(transferRepository).save(any(Transfer.class));
    }

    @Test
    void transfer_insufficientBalance_throwsException() {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        TransferRequest request = new TransferRequest(fromId, toId, BigDecimal.valueOf(1000));

        User user = new User();
        user.setId(UUID.randomUUID());

        Card fromCard = new Card();
        fromCard.setUser(user);
        fromCard.setBalance(BigDecimal.valueOf(500));

        Card toCard = new Card();
        toCard.setUser(user);
        toCard.setBalance(BigDecimal.ZERO);

        when(cardRepository.findById(fromId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toId)).thenReturn(Optional.of(toCard));

        assertThrows(IllegalArgumentException.class, () -> transferService.transfer(request));
    }

    @Test
    void transfer_cardsOfDifferentUsers_throwsException() {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        TransferRequest request = new TransferRequest(fromId, toId, BigDecimal.valueOf(100));

        User user1 = new User();
        user1.setId(UUID.randomUUID());

        User user2 = new User();
        user2.setId(UUID.randomUUID());

        Card fromCard = new Card();
        fromCard.setUser(user1);
        fromCard.setBalance(BigDecimal.valueOf(500));

        Card toCard = new Card();
        toCard.setUser(user2);
        toCard.setBalance(BigDecimal.ZERO);

        when(cardRepository.findById(fromId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toId)).thenReturn(Optional.of(toCard));

        assertThrows(IllegalArgumentException.class, () -> transferService.transfer(request));
    }

}
