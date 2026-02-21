package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {

    List<Card> findByUserId(UUID userId);
    Page<Card> findByUserId(UUID userId, Pageable pageable);
    Page<Card> findByUserIdAndStatus(UUID userId, String status, Pageable pageable);
    boolean existsByMaskedNumber(String maskedNumber);

}
