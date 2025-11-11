/**
 * Class Name: AuthenticationController
 * Package: com.musicApp.backend.features.authentication.controller
 * Date: November 10, 2025
 * @author Jose Bastidas
 *
 * Data Structures:
 * - AuthenticationUser: Entity representing the application’s user model.
 * - AuthenticationRequestBody / AuthenticationResponseBody: DTOs for login and registration payloads.
 * - List<AuthenticationUser>: Used to return multiple active users.
 * - JWT (JSON Web Token): Used for stateless user authentication.
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
     * <p>
     *     Input: AuthenticationUser (from request attribute)
     * <p>
     *     Output: AuthenticationUser (user data)
     * @param authenticationUser
     * @return
     */
    @GetMapping("/user")
    public AuthenticationUser getUser(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser) {
        return authenticationService.getUser(authenticationUser.getEmail());
    }

    /**
     *  Handles user login and JWT token generation.
     *     Input: AuthenticationRequestBody (email, password)
     *     Output: AuthenticationResponseBody (JWT token and user info)
     * @param loginRequestBody
     * @return
     */
    @PostMapping("/login")
    public AuthenticationResponseBody loginPage(@Valid @RequestBody AuthenticationRequestBody loginRequestBody) {
        return authenticationService.login(loginRequestBody);
    }

    /**
     * Handles new user registration and sends a verification email.
     *     Input: AuthenticationRequestBody (registration data)
     *     Output: AuthenticationResponseBody
     *     Throws: MessagingException, UnsupportedEncodingException
     * @param registerRequestBody
     * @return
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    @PostMapping("/register")
    public AuthenticationResponseBody registerPage(@Valid @RequestBody AuthenticationRequestBody registerRequestBody)
            throws MessagingException, UnsupportedEncodingException {
        System.out.println("Received register request for: " + registerRequestBody.getEmail());
        return authenticationService.register(registerRequestBody);
    }

    /**
     *  Logs out a user by invalidating their JWT token.
     *     Input: Authorization header with Bearer token
     *     Output: HTTP response message
     * @param authHeader
     * @return
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
    *       Input: None
    *      Output: {@code List<AuthenticationUser>}

    * @return
    */
    @GetMapping("/online-users")
    public List<AuthenticationUser> getOnlineUsers() {
        return authenticationService.getOnlineUsers();
    }


    /**
     *   Validates an email verification token, marks the user as verified, and activates login status.
     *     Input: token (verification token), Authorization header (JWT)
     *     Output: HTTP response message
     * @param token
     * @param authHeader
     * @return
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
     *     Input: authenticated user (from request attribute)
     *     Output: success message string
     * @param user
     * @return
     */
    @GetMapping("/send-email-verification-token")
    public String sendEmailVerificationToken(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        authenticationService.sendEmailVerificationToken(user.getEmail());
        return "Email verification token sent successfully.";
    }

    /**
     * Sends a password reset token to the given email address.
     *     Input: email
     *     Output: success message string
     * @param email
     * @return
     */
    @PutMapping("/send-password-reset-token")
    public String sendPasswordResetToken(@RequestParam String email) {
        authenticationService.sendPasswordResetToken(email);
        return "Password reset token sent successfully.";
    }

    /**
     *     Resets the user’s password if a valid reset token is provided.
     *     Input: new password, reset token, email
     *     Output: success message string
     * @param newPassword
     * @param token
     * @param email
     * @return
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
    *     Input: Authorization header (JWT)
    *     Output: HTTP response message
    * @param authHeader
    * @return
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
