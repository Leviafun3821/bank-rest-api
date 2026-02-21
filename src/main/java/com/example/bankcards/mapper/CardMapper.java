package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", constant = Card.STATUS_ACTIVE)
    @Mapping(target = "balance", constant = "0.00")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "encryptedCardNumber", source = "cardNumber", qualifiedByName = "encryptCardNumber")
    @Mapping(target = "maskedNumber", source = "cardNumber", qualifiedByName = "maskCardNumber")
    @Mapping(target = "expiryDate", source = "expiryDate", qualifiedByName = "parseExpiryDate")
    Card toEntity(CreateCardRequest request);

    CardDTO toDTO(Card card);

    @Named("maskCardNumber")
    default String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    @Named("encryptCardNumber")
    default String encryptCardNumber(String cardNumber) {
        return cardNumber; // заглушка
    }

    @Named("parseExpiryDate")
    default LocalDate parseExpiryDate(String expiryDate) {
        if (expiryDate == null || !expiryDate.matches("\\d{2}/\\d{2}")) {
            throw new IllegalArgumentException("Invalid expiry date format: " + expiryDate + ". Use MM/YY");
        }
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt("20" + parts[1]); // 20YY
        return LocalDate.of(year, month, 1); // берём 1-е число месяца
    }

}
