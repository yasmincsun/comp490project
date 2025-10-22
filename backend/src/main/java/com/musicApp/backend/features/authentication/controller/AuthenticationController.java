package com.musicApp.backend.features.authentication.controller;

import com.musicApp.backend.features.authentication.dto.AuthenticationRequestBody;
import com.musicApp.backend.features.authentication.dto.AuthenticationResponseBody;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.service.AuthenticationService;
import com.musicApp.backend.features.authentication.utils.EmailService;
import com.musicApp.backend.features.authentication.utils.JsonWebToken;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;

@CrossOrigin(origins = "http://localhost:5173") // adjust if frontend uses a different port
@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JsonWebToken jsonWebToken;
    private final EmailService emailService;

    public AuthenticationController(AuthenticationService authenticationService,
                                    JsonWebToken jsonWebToken,
                                    EmailService emailService) {
        this.authenticationService = authenticationService;
        this.jsonWebToken = jsonWebToken;
        this.emailService = emailService;
    }

    @GetMapping("/user")
    public AuthenticationUser getUser(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser) {
        return authenticationService.getUser(authenticationUser.getEmail());
    }

    @PostMapping("/login")
    public AuthenticationResponseBody loginPage(@Valid @RequestBody AuthenticationRequestBody loginRequestBody) {
        return authenticationService.login(loginRequestBody);
    }

    @PostMapping("/register")
    public AuthenticationResponseBody registerPage(@Valid @RequestBody AuthenticationRequestBody registerRequestBody)
            throws MessagingException, UnsupportedEncodingException {
        System.out.println("🟢 Received register request for: " + registerRequestBody.getEmail());
        return authenticationService.register(registerRequestBody);
    }

    // @PutMapping("/validate-email-verification-token")
    // public String verifyEmail(@RequestParam String token,
    //                           @RequestAttribute("authenticatedUser") AuthenticationUser user) {
    //     authenticationService.validateEmailVerificationToken(token, user.getEmail());
    //     return "Email verified successfully.";
    // }

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

        return ResponseEntity.ok("Email verified successfully.");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("Server error: " + e.getMessage());
    }
}


    @GetMapping("/send-email-verification-token")
    public String sendEmailVerificationToken(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        authenticationService.sendEmailVerificationToken(user.getEmail());
        return "Email verification token sent successfully.";
    }

    @PutMapping("/send-password-reset-token")
    public String sendPasswordResetToken(@RequestParam String email) {
        authenticationService.sendPasswordResetToken(email);
        return "Password reset token sent successfully.";
    }

    @PutMapping("/reset-password")
    public String resetPassword(@RequestParam String newPassword,
                                @RequestParam String token,
                                @RequestParam String email) {
        authenticationService.resetPassword(email, newPassword, token);
        return "Password reset successfully.";
    }

    // ✅ FIXED resend verification endpoint
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
