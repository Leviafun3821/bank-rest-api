package com.example.bankcards.service.impl;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;

    @Override
    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        // Находим карты
        Card fromCard = cardRepository.findById(request.fromCardId())
                .orElseThrow(() -> new CardNotFoundException("From card not found: " + request.fromCardId()));
        Card toCard = cardRepository.findById(request.toCardId())
                .orElseThrow(() -> new CardNotFoundException("To card not found: " + request.toCardId()));

        // Проверка: карты принадлежат одному пользователю
        if (!fromCard.getUser().getId().equals(toCard.getUser().getId())) {
            throw new IllegalArgumentException("Transfer allowed only between cards of the same user");
        }

        // Проверка баланса
        if (fromCard.getBalance().compareTo(request.amount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance on card: " + request.fromCardId());
        }

        // Выполняем перевод
        fromCard.setBalance(fromCard.getBalance().subtract(request.amount()));
        toCard.setBalance(toCard.getBalance().add(request.amount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        // Сохраняем перевод
        Transfer transfer = Transfer.builder()
                .fromCard(fromCard)
                .toCard(toCard)
                .amount(request.amount())
                .timestamp(LocalDateTime.now())
                .status("SUCCESS")
                .build();

        transfer = transferRepository.save(transfer);

        return new TransferResponse(
                transfer.getId(),
                fromCard.getId(),
                toCard.getId(),
                request.amount(),
                transfer.getTimestamp(),
                transfer.getStatus()
        );
    }

}
