package com.example.bankcards.controller;

import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.dto.LoginResponse;
import com.example.bankcards.service.auth.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_successful_shouldReturn200AndSetJwtCookie() {
        LoginRequest request = new LoginRequest("admin", "admin123");
        String token = "jwt-token-example";

        when(authenticationService.authenticateAndGenerateToken("admin", "admin123")).thenReturn(token);

        ResponseEntity<LoginResponse> result = authController.login(request, response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Login successful", result.getBody().message());

        verify(authenticationService).authenticateAndGenerateToken("admin", "admin123");
        verify(response).addCookie(argThat(cookie ->
                "jwt".equals(cookie.getName()) &&
                        token.equals(cookie.getValue()) &&
                        cookie.isHttpOnly() &&
                        !cookie.getSecure() &&
                        "/".equals(cookie.getPath()) &&
                        86400 == cookie.getMaxAge()
        ));
    }

    @Test
    void login_invalidCredentials_shouldThrowExceptionFromService() {
        LoginRequest request = new LoginRequest("admin", "wrongpass");

        when(authenticationService.authenticateAndGenerateToken("admin", "wrongpass"))
                .thenThrow(new RuntimeException("Invalid username or password"));

        assertThrows(RuntimeException.class, () -> authController.login(request, response));
        verify(response, never()).addCookie(any(Cookie.class));
    }

}
