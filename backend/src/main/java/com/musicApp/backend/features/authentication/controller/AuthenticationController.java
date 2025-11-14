/**
 * Date: November 10, 2025
 * @author Jose Bastidas
 *
 * Data Structures:
 * - List<AuthenticationUser>: Used to return multiple active users.
 *
 * Algorithms:
 * - Token-based authentication flow:
 *     The system uses JWTs for stateless authentication, allowing secure user identification without
 *     maintaining server-side sessions.
 *     Chosen for scalability and compatibility with modern REST architectures.
 * - Email verification and password reset rely on random token generation and time-based validation
 *     implemented in the service layer.
 */

package com.musicApp.backend.features.authentication.controller;

import com.musicApp.backend.features.authentication.dto.AuthenticationRequestBody;
import com.musicApp.backend.features.authentication.dto.AuthenticationResponseBody;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.service.AuthenticationService;
import com.musicApp.backend.features.authentication.utils.EmailService;
import com.musicApp.backend.features.authentication.utils.JsonWebToken;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;
import java.util.List;


/**
 * This controller manages all authentication-related endpoints for the MusicApp backend.
 * It handles user registration, login, logout, email verification, and password reset operations.<br>
 * 
 * The controller interacts with the AuthenticationService layer to perform business logic,
 * and uses the JsonWebToken utility for JWT-based authentication and the EmailService for
 * sending verification and password reset emails.
 */
@CrossOrigin(origins = "http://localhost:5173") // adjust if frontend uses a different port
@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JsonWebToken jsonWebToken;
    private final EmailService emailService;
    private final AuthenticationUserRepository authenticationUserRepository;

    @Autowired
    public AuthenticationController(AuthenticationUserRepository authenticationUserRepository,    
                                    AuthenticationService authenticationService,
                                    JsonWebToken jsonWebToken,
                                    EmailService emailService) {
        this.authenticationUserRepository = authenticationUserRepository;
        this.authenticationService = authenticationService;
        this.jsonWebToken = jsonWebToken;
        this.emailService = emailService;
    }

    /**
     * Retrieves the currently authenticated user’s information.
     * @param authenticationUser the authenticated user object retrieved from the request attribute
     * @return the full {@link AuthenticationUser} information corresponding to the user's email
     */
    @GetMapping("/user")
    public AuthenticationUser getUser(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser) {
        return authenticationService.getUser(authenticationUser.getEmail());
    }

    /**
     *  Handles user login and JWT token generation.
     * @param loginRequestBody the {@link AuthenticationRequestBody} containing user email and password
     * @return an {@link AuthenticationResponseBody} with the JWT token and basic user details
     */
    @PostMapping("/login")
    public AuthenticationResponseBody loginPage(@Valid @RequestBody AuthenticationRequestBody loginRequestBody) {
        return authenticationService.login(loginRequestBody);
    }

    /**
     * Handles new user registration and sends a verification email.
     * @param registerRequestBody the {@link AuthenticationRequestBody} containing registration data
     * @return an {@link AuthenticationResponseBody} with user info and confirmation message
     * @throws MessagingException if there is an issue while sending the verification email
     * @throws UnsupportedEncodingException if the email encoding is unsupported
     */
    @PostMapping("/register")
    public AuthenticationResponseBody registerPage(@Valid @RequestBody AuthenticationRequestBody registerRequestBody)
            throws MessagingException, UnsupportedEncodingException {
        System.out.println("Received register request for: " + registerRequestBody.getEmail());
        return authenticationService.register(registerRequestBody);
    }

    /**
     *  Logs out a user by invalidating their JWT token.
     * @param authHeader the "Authorization" HTTP header containing the Bearer token
     * @return a {@link ResponseEntity} containing a success or failure message
     */
    @PutMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            authenticationService.logout(token);
            return ResponseEntity.ok("User logged out successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Logout failed: " + e.getMessage());
        }
    }

    /**
    *     Retrieves all currently active (logged-in) users.
     * @return a list of {@link AuthenticationUser} objects representing active users
     */
    @GetMapping("/online-users")
    public List<AuthenticationUser> getOnlineUsers() {
        return authenticationService.getOnlineUsers();
    }


    /**
     *   Validates an email verification token, marks the user as verified, and activates login status.
     * @param token the email verification token submitted by the user
     * @param authHeader the "Authorization" header containing the JWT token
     * @return a {@link ResponseEntity} indicating success or failure of verification
     */
    @PutMapping("/validate-email-verification-token")
    public ResponseEntity<String> verifyEmail(
            @RequestParam String token,
            @RequestHeader("Authorization") String authHeader) {

        try {
            // Extract the JWT from the header
            String jwt = authHeader.replace("Bearer ", "");

            // Get the email from the JWT
            String email = jsonWebToken.getEmailFromToken(jwt);

            // Validate the verification code
            authenticationService.validateEmailVerificationToken(token, email);

            // Update the user's login status after successful verification
            AuthenticationUser user = authenticationUserRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setLoginStatus(true); // mark as "active" now
            authenticationUserRepository.save(user);

            return ResponseEntity.ok("Email verified successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Server error: " + e.getMessage());
        }
    }


    /**
     *   Sends a new email verification token to the authenticated user.
     * @param user the currently authenticated {@link AuthenticationUser}
     * @return a confirmation message indicating that the token was sent
     */
    @GetMapping("/send-email-verification-token")
    public String sendEmailVerificationToken(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        authenticationService.sendEmailVerificationToken(user.getEmail());
        return "Email verification token sent successfully.";
    }

    /**
     * Sends a password reset token to the given email address.
     * @param email the email address to which the password reset token will be sent
     * @return a confirmation message indicating that the token was sent
     */
    @PutMapping("/send-password-reset-token")
    public String sendPasswordResetToken(@RequestParam String email) {
        authenticationService.sendPasswordResetToken(email);
        return "Password reset token sent successfully.";
    }

    /**
     *     Resets the user’s password if a valid reset token is provided.
     * @param newPassword the new password to be set
     * @param token the password reset token provided to the user
     * @param email the email address associated with the account
     * @return a confirmation message indicating the password was successfully reset
     */
    @PutMapping("/reset-password")
    public String resetPassword(@RequestParam String newPassword,
                                @RequestParam String token,
                                @RequestParam String email) {
        authenticationService.resetPassword(email, newPassword, token);
        return "Password reset successfully.";
    }


    /**
     *     Resends the email verification token using JWT-derived email.
     * @param authHeader the "Authorization" header containing the user's JWT
     * @return a {@link ResponseEntity} indicating whether the resend was successful or not
     */

    // FIXED resend verification endpoint
    @PostMapping("/resend-email-verification")
    public ResponseEntity<String> resendVerificationEmail(@RequestHeader("Authorization") String authHeader) {
        // Extract the token from header
        String token = authHeader.replace("Bearer ", "");
        String email = jsonWebToken.getEmailFromToken(token);

        try {
            authenticationService.sendEmailVerificationToken(email);
            return ResponseEntity.ok("Verification email resent successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to resend verification email: " + e.getMessage());
        }
    }




}
