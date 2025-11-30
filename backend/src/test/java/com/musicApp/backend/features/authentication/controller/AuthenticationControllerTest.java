package com.musicApp.backend.features.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicApp.backend.features.authentication.dto.AuthenticationRequestBody;
import com.musicApp.backend.features.authentication.dto.AuthenticationResponseBody;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;
import com.musicApp.backend.features.authentication.service.AuthenticationService;
import com.musicApp.backend.features.authentication.utils.EmailService;
import com.musicApp.backend.features.authentication.utils.JsonWebToken;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JsonWebToken jsonWebToken;

    @MockBean
    private AuthenticationUserRepository userRepository;

    @MockBean
    private EmailService emailService;

    // -----------------------------------------------------
    // LOGIN TESTS                              
    // -----------------------------------------------------

    // Successful Login
    @Test
    void loginPage_Success() throws Exception {

        AuthenticationRequestBody request = new AuthenticationRequestBody(
                "Test",
                "Testing",
                "test",
                "test@example.com",
                "password"
        );

        AuthenticationResponseBody response = new AuthenticationResponseBody(
                "jwtToken",
                "Authentication succeeded.",
                "test",
                "test@example.com",
                true
        );

        when(authenticationService.login(any(AuthenticationRequestBody.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwtToken"))
                .andExpect(jsonPath("$.username").value("test"));
    }

    //Failed Login
    @Test
    void loginPage_Failure() throws Exception {

        AuthenticationRequestBody request = new AuthenticationRequestBody(
                "Test",
                "Testing",
                "test",
                "test@example.com",
                "wrongPass"
        );

        when(authenticationService.login(any(AuthenticationRequestBody.class)))
                .thenThrow(new IllegalArgumentException("Password is incorrect."));

        mockMvc.perform(post("/api/v1/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password is incorrect."));

    }

    // -----------------------------------------------------
    // EMAIL VERIFICATION TESTS           verifyEmail method
    // -----------------------------------------------------

    // Successful Token
    @Test
    void verifyEmail_Success() throws Exception {

        String jwt = "jwtToken";
        String token = "12345";
        String email = "test@example.com";

        AuthenticationUser user = new AuthenticationUser();
        user.setEmail(email);
        user.setLoginStatus(false);

        when(jsonWebToken.getEmailFromToken(jwt)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        doNothing().when(authenticationService).validateEmailVerificationToken(token, email);
        when(userRepository.save(any(AuthenticationUser.class))).thenReturn(user);

        mockMvc.perform(put("/api/v1/authentication/validate-email-verification-token")
                        .param("token", token)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(content().string("Email verified successfully."));
    }

    // Invalid Token
    @Test
    void verifyEmail_Failure_InvalidToken() throws Exception {

        String jwt = "jwtToken";
        String token = "12345";
        String email = "test@example.com";

        when(jsonWebToken.getEmailFromToken(jwt)).thenReturn(email);
        doThrow(new IllegalArgumentException("Invalid email verification token."))
                .when(authenticationService)
                .validateEmailVerificationToken(token, email);

        mockMvc.perform(put("/api/v1/authentication/validate-email-verification-token")
                        .param("token", token)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid email verification token."));
    }

        // Path 3: User Not Found / Server Error
        @Test
        void verifyEmail_Failure_UserNotFound() throws Exception {

        String jwt = "jwtToken";
        String token = "12345";
        String email = "test@example.com";

        // Mock JWT extraction to return email
        when(jsonWebToken.getEmailFromToken(jwt)).thenReturn(email);

        // Mock token validation to succeed
        doNothing().when(authenticationService).validateEmailVerificationToken(token, email);

        // Mock user repository to return empty (user not found)
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Perform the PUT request
        mockMvc.perform(put("/api/v1/authentication/validate-email-verification-token")
                        .param("token", token)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Server error: User not found"));
        }





}

