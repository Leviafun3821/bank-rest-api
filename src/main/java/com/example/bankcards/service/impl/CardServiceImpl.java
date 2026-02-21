package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.UpdateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;
    private final CardEncryptor cardEncryptor;

    @Override
    public CardDTO createCard(CreateCardRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(UserNotFoundException::new);

        Card card = cardMapper.toEntity(request);
        String encryptedNumber = cardEncryptor.encrypt(request.cardNumber());
        String maskedNumber = cardMapper.maskCardNumber(request.cardNumber());
        card.setEncryptedCardNumber(encryptedNumber);
        card.setMaskedNumber(maskedNumber);

        card.setUser(user);
        card.setStatus(Card.STATUS_ACTIVE);
        card.setBalance(BigDecimal.ZERO);

        Card saved = cardRepository.save(card);
        return cardMapper.toDTO(saved);
    }

    @Override
    public Page<CardDTO> getMyCards(Pageable pageable, String status) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(UserNotFoundException::new);

        if (status != null && !status.isBlank()) {
            return cardRepository.findByUserIdAndStatus(user.getId(), status, pageable)
                    .map(cardMapper::toDTO);
        } else {
            return cardRepository.findByUserId(user.getId(), pageable)
                    .map(cardMapper::toDTO);
        }
    }

    @Override
    public CardDTO getCardById(UUID id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(CardNotFoundException::new);
        return cardMapper.toDTO(card);
    }

    @Override
    public CardDTO updateCard(UUID id, UpdateCardRequest request) {
        Card card = cardRepository.findById(id)
                .orElseThrow(CardNotFoundException::new);
        card.setStatus(request.status());
        Card saved = cardRepository.save(card);
        return cardMapper.toDTO(saved);
    }

    @Override
    public void blockCard(UUID id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(CardNotFoundException::new);
        card.setStatus(Card.STATUS_BLOCKED);
        cardRepository.save(card);
    }

    @Override
    public void unblockCard(UUID id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(CardNotFoundException::new);
        card.setStatus(Card.STATUS_ACTIVE);
        cardRepository.save(card);
    }

    @Override
    public Page<CardDTO> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(cardMapper::toDTO);
    }

    @Override
    public CardDTO createCardForUser(UUID userId, CreateCardRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Card card = cardMapper.toEntity(request);
        String encryptedNumber = cardEncryptor.encrypt(request.cardNumber());
        String maskedNumber = cardMapper.maskCardNumber(request.cardNumber());

        card.setEncryptedCardNumber(encryptedNumber);
        card.setMaskedNumber(maskedNumber);
        card.setUser(user);
        card.setStatus(Card.STATUS_ACTIVE);
        card.setBalance(BigDecimal.ZERO);

        Card saved = cardRepository.save(card);
        return cardMapper.toDTO(saved);
    }

}
