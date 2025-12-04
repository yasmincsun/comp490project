package com.musicApp.backend.features.authentication.service;

import com.musicApp.backend.features.authentication.utils.Encoder;
import java.time.LocalDateTime;
import com.musicApp.backend.features.authentication.dto.AuthenticationRequestBody;
import com.musicApp.backend.features.authentication.dto.AuthenticationResponseBody;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;
import com.musicApp.backend.features.authentication.utils.JsonWebToken;

import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationUserRepository userRepository;

    @Mock
    private Encoder encoder;

    @Mock
    private JsonWebToken jwt;

    @InjectMocks
    private AuthenticationService authService;

    private AuthenticationUser testUser;

    @BeforeEach
    void setup() {
        testUser = new AuthenticationUser("Test", "Testing", "test", "test@example.com", "encodedPass");
        testUser.setEmailVerified(false);
        testUser.setEmailVerificationToken("hashedToken");
        testUser.setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(5));
    }

    // -------------------------
    // validateEmailVerificationToken tests
    // -------------------------

    // User not found
    @Test
    void validateEmailVerificationToken_UserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> authService.validateEmailVerificationToken("12345", "notfound@example.com"));
    }

    // Wrong Token
    @Test
    void validateEmailVerificationToken_InvalidToken() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(encoder.matches("wrongToken", testUser.getEmailVerificationToken())).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> authService.validateEmailVerificationToken("wrongToken", "test@example.com"));
    }
    
    // Expired Token
    @Test
    void validateEmailVerificationToken_ExpiredToken() {
        testUser.setEmailVerificationTokenExpiryDate(LocalDateTime.now().minusMinutes(1));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(encoder.matches("12345", testUser.getEmailVerificationToken())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> authService.validateEmailVerificationToken("12345", "test@example.com"));
    }

    // Successful Token
    @Test
    void validateEmailVerificationToken_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(encoder.matches("12345", testUser.getEmailVerificationToken())).thenReturn(true);
        authService.validateEmailVerificationToken("12345", "test@example.com");
        assertTrue(testUser.getEmailVerified());
    }

    // -------------------------
    // login tests
    // -------------------------

    // User not found
    @Test
    void login_UserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        AuthenticationRequestBody loginReq = new AuthenticationRequestBody();
        loginReq.setEmail("notfound@example.com");
        loginReq.setPassword("password");
        assertThrows(IllegalArgumentException.class, () -> authService.login(loginReq));
    }

    // Wrong Password
    @Test
    void login_InvalidPassword() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(encoder.matches("wrongPass", testUser.getPassword())).thenReturn(false);
        AuthenticationRequestBody loginReq = new AuthenticationRequestBody();
        loginReq.setEmail("test@example.com");
        loginReq.setPassword("wrongPass");
        assertThrows(IllegalArgumentException.class, () -> authService.login(loginReq));
    }

    // Successful login
    @Test
    void login_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(encoder.matches("password", testUser.getPassword())).thenReturn(true);
        when(jwt.generateToken("test@example.com")).thenReturn("jwtToken");

        AuthenticationRequestBody loginReq = new AuthenticationRequestBody();
        loginReq.setEmail("test@example.com");
        loginReq.setPassword("password");

        AuthenticationResponseBody response = authService.login(loginReq);
        assertEquals("jwtToken", response.getToken());
        assertTrue(testUser.isLoginStatus());
    }
}
