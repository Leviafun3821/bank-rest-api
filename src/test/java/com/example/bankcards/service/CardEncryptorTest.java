package com.example.bankcards.service;

import com.example.bankcards.util.CardEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CardEncryptorTest {

    private CardEncryptor cardEncryptor;

    @BeforeEach
    void setUp() {
        cardEncryptor = new CardEncryptor();
        ReflectionTestUtils.setField(cardEncryptor, "secretKey", "ThisIsMySuperSecret16ByteKey1234");
    }

    @Test
    void encryptAndDecrypt_shouldWorkCorrectly() {
        String original = "4111111111111111";

        String encrypted = cardEncryptor.encrypt(original);
        assertNotNull(encrypted);
        assertNotEquals(original, encrypted);

        String decrypted = cardEncryptor.decrypt(encrypted);
        assertEquals(original, decrypted);
    }

}
