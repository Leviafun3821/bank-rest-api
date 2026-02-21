package com.example.bankcards.service;

import java.util.UUID;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.UpdateCardRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {

    CardDTO createCard(CreateCardRequest request);
    Page<CardDTO> getMyCards(Pageable pageable, String status);
    CardDTO getCardById(UUID id);
    CardDTO updateCard(UUID id, UpdateCardRequest request);
    void blockCard(UUID id);
    void unblockCard(UUID id);
    Page<CardDTO> getAllCards(Pageable pageable);
    CardDTO createCardForUser(UUID userId, CreateCardRequest request);

}
