package com.example.bankcards.service.auth;

public interface AuthenticationService {
    String authenticateAndGenerateToken(String username, String password);
}
