package com.example.bankcards.exception;

public class UserNotFoundException extends RuntimeException {

    private static final String MESSAGE = "User not found";

    public UserNotFoundException() {
        super(MESSAGE);
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
