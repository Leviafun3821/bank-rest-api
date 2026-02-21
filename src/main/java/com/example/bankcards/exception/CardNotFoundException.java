package com.example.bankcards.exception;

public class CardNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Card not found";

    public CardNotFoundException() {
        super(MESSAGE);
    }

    public CardNotFoundException(String message) {
        super(message);
    }

    public CardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
