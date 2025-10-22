package com.musicApp.backend.features.authentication.service;

import com.musicApp.backend.features.authentication.dto.AuthenticationRequestBody;
import com.musicApp.backend.features.authentication.dto.AuthenticationResponseBody;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;
import com.musicApp.backend.features.authentication.utils.EmailService;
import com.musicApp.backend.features.authentication.utils.Encoder;
import com.musicApp.backend.features.authentication.utils.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final AuthenticationUserRepository authenticationUserRepository;
    private final int durationInMinutes = 10;

    private final JsonWebToken jsonWebToken;
    private final Encoder encoder;
    //private final AuthenticationUserRepository authenticationUserRepository;
    private final EmailService emailService;

    public AuthenticationService(JsonWebToken jsonWebToken, Encoder encoder, AuthenticationUserRepository authenticationUserRepository, EmailService emailService){
        this.jsonWebToken = jsonWebToken;
        this.encoder = encoder;
        this.authenticationUserRepository = authenticationUserRepository;
        this.emailService = emailService;
    }

    public static String generateEmailVerificationToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            token.append(random.nextInt(10));
        }
        return token.toString();
    }

    // public void sendEmailVerificationToken(String email) {
    //     Optional<AuthenticationUser> user = authenticationUserRepository.findByEmail(email);
    //     if (user.isPresent() && !user.get().getEmailVerified()) {
    //         String emailVerificationToken = generateEmailVerificationToken();
    //         String hashedToken = encoder.encode(emailVerificationToken);
    //         user.get().setEmailVerificationToken(hashedToken);
    //         user.get().setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));
    //         authenticationUserRepository.save(user.get());
    //         String subject = "Email Verification";
    //         String body = String.format("Only one step to take full advantage of the Moody APP.\n\n"
    //                         + "Enter this code to verify your email: " + "%s\n\n" + "The code will expire in " + "%s"
    //                         + " minutes.",
    //                 emailVerificationToken, durationInMinutes);
    //         try {
    //             emailService.sendEmail(email, subject, body);
    //         } catch (Exception e) {
    //             logger.info("Error while sending email: {}", e.getMessage());
    //         }
    //     } else {
    //         throw new IllegalArgumentException("Email verification token failed, or email is already verified.");
    //     }
    // }


public void sendEmailVerificationToken(String email) {
    Optional<AuthenticationUser> userOpt = authenticationUserRepository.findByEmail(email);

    if (userOpt.isEmpty()) {
        throw new IllegalArgumentException("User not found.");
    }

    AuthenticationUser user = userOpt.get();

    if (user.getEmailVerified()) {
        throw new IllegalArgumentException("Email already verified.");
    }

    String emailVerificationToken = generateEmailVerificationToken();
    String hashedToken = encoder.encode(emailVerificationToken);
    user.setEmailVerificationToken(hashedToken);
    user.setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));
    authenticationUserRepository.save(user);

    System.out.println("Generated token for " + email + ": " + emailVerificationToken); // 🔹 debug

    String subject = "Email Verification";
    String body = String.format(
        "Only one step to take full advantage of Moody.\n\nEnter this code to verify your email: %s. The code will expire in %d minutes.",
        emailVerificationToken, durationInMinutes
    );

    try {
        emailService.sendEmail(email, subject, body);
    } catch (Exception e) {
        System.err.println("Error sending verification email: " + e.getMessage());
    }
}









    // public void validateEmailVerificationToken(String token, String email) {
    //     Optional<AuthenticationUser> user = authenticationUserRepository.findByEmail(email);
    //     if (user.isPresent() && encoder.matches(token, user.get().getEmailVerificationToken())
    //             && !user.get().getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now())) {
    //         user.get().setEmailVerified(true);
    //         user.get().setEmailVerificationToken(null);
    //         user.get().setEmailVerificationTokenExpiryDate(null);
    //         authenticationUserRepository.save(user.get());
    //     } else if (user.isPresent() && encoder.matches(token, user.get().getEmailVerificationToken())
    //             && user.get().getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now())) {
    //         throw new IllegalArgumentException("Email verification token expired.");
    //     } else {
    //         throw new IllegalArgumentException("Email verification token failed.");
    //     }
    // }





public void validateEmailVerificationToken(String token, String email) {
    Optional<AuthenticationUser> userOpt = authenticationUserRepository.findByEmail(email);

    if (userOpt.isEmpty()) {
        throw new IllegalArgumentException("User not found.");
    }

    AuthenticationUser user = userOpt.get();
    System.out.println("Submitted token: " + token);
    System.out.println("Saved hashed token: " + user.getEmailVerificationToken());

    if (encoder.matches(token, user.getEmailVerificationToken())) {
        if (user.getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Email verification token expired.");
        }
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiryDate(null);
        authenticationUserRepository.save(user);
        System.out.println("Email verified for: " + email);
    } else {
        throw new IllegalArgumentException("Invalid email verification token.");
    }
}








    public AuthenticationUser getUser(String email){
        return authenticationUserRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

//    public AuthenticationResponseBody register(AuthenticationRequestBody registerRequestBody) throws MessagingException, UnsupportedEncodingException {
//        authenticationUserRepository.save(new AuthenticationUser(registerRequestBody.getEmail(), encoder.encode(registerRequestBody.getPassword())));
//        String token = jsonWebToken.generateToken(registerRequestBody.getEmail());
//        emailService.sendEmail(registerRequestBody.getEmail(), "Some subject", "Somebody");
//        return new AuthenticationResponseBody(token, "User registered successfully");
//    }

public AuthenticationResponseBody register(AuthenticationRequestBody registerRequestBody) {
    // Save new user with name, email, and encoded password
    AuthenticationUser user = authenticationUserRepository.save(
            new AuthenticationUser(
                    registerRequestBody.getName(), // 👈 added name
                    registerRequestBody.getEmail(),
                    encoder.encode(registerRequestBody.getPassword())
            )
    );

    // Generate email verification token
    String emailVerificationToken = generateEmailVerificationToken();
    String hashedToken = encoder.encode(emailVerificationToken);
    user.setEmailVerificationToken(hashedToken);
    user.setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));

    authenticationUserRepository.save(user);

    // Prepare email message
    String subject = "Email Verification";
    String body = String.format("""
            Only one step to take full advantage of Moody.

            Enter this code to verify your email: %s. The code will expire in %s minutes.""",
            emailVerificationToken, durationInMinutes);
    try {
        emailService.sendEmail(registerRequestBody.getEmail(), subject, body);
    } catch (Exception e) {
        logger.info("Error while sending email: {}", e.getMessage());
    }

    // Generate JWT token
    String authToken = jsonWebToken.generateToken(registerRequestBody.getEmail());

    return new AuthenticationResponseBody(authToken, "User registered successfully.");
}





    public void sendPasswordResetToken(String email) {
        Optional<AuthenticationUser> user = authenticationUserRepository.findByEmail(email);
        if (user.isPresent()) {
            String passwordResetToken = generateEmailVerificationToken();
            String hashedToken = encoder.encode(passwordResetToken);
            user.get().setPasswordResetToken(hashedToken);
            user.get().setPasswordResetTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));
            authenticationUserRepository.save(user.get());
            String subject = "Password Reset";
            String body = String.format("""
                    You requested a password reset.

                    Enter this code to reset your password: %s. The code will expire in %s minutes.""",
                    passwordResetToken, durationInMinutes);
            try {
                emailService.sendEmail(email, subject, body);
            } catch (Exception e) {
                logger.info("Error while sending email: {}", e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    public void resetPassword(String email, String newPassword, String token) {
        Optional<AuthenticationUser> user = authenticationUserRepository.findByEmail(email);
        if (user.isPresent() && encoder.matches(token, user.get().getPasswordResetToken())
                && !user.get().getPasswordResetTokenExpiryDate().isBefore(LocalDateTime.now())) {
            user.get().setPasswordResetToken(null);
            user.get().setPasswordResetTokenExpiryDate(null);
            user.get().setPassword(encoder.encode(newPassword));
            authenticationUserRepository.save(user.get());
        } else if (user.isPresent() && encoder.matches(token, user.get().getPasswordResetToken())
                && user.get().getPasswordResetTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Password reset token expired.");
        } else {
            throw new IllegalArgumentException("Password reset token failed.");
        }
    }





    public AuthenticationResponseBody login(AuthenticationRequestBody loginRequestBody) {
        AuthenticationUser user = authenticationUserRepository.findByEmail(loginRequestBody.getEmail()).orElseThrow(() -> new IllegalArgumentException("User not found."));
        if (!encoder.matches(loginRequestBody.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Password is incorrect.");
        }
        String token = jsonWebToken.generateToken(loginRequestBody.getEmail());
        return new AuthenticationResponseBody(token, "Authentication succeeded. ");
    }

    


}
