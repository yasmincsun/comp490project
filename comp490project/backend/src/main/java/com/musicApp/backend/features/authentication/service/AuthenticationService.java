/**
 * Date: September 25, 2025
 * @author Jose Bastidas
 * 
 **/

package com.musicApp.backend.features.authentication.service;

import com.musicApp.backend.features.authentication.dto.AuthenticationRequestBody;
import com.musicApp.backend.features.authentication.dto.AuthenticationResponseBody;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;
import com.musicApp.backend.features.authentication.utils.EmailService;
import com.musicApp.backend.features.authentication.utils.Encoder;
import com.musicApp.backend.features.authentication.utils.JsonWebToken;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * This service class provides the core authentication and user management logic
 * for the MusicApp backend. It handles registration, login, logout, email verification,
 * password resets, and retrieval of online users. <br>
 *
 * It integrates with:<br>
 * - AuthenticationUserRepository for database operations<br>
 * - Encoder for password/token hashing<br>
 * - JsonWebToken for JWT generation and verification<br>
 * - EmailService for sending emails to users<br>
 */
@Service
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final AuthenticationUserRepository authenticationUserRepository;
    private final int durationInMinutes = 10;

    private final JsonWebToken jsonWebToken;
    private final Encoder encoder;
    //private final AuthenticationUserRepository authenticationUserRepository;
    private final EmailService emailService;

    @PersistenceContext
    private EntityManager entityManager;

    public AuthenticationService(JsonWebToken jsonWebToken, Encoder encoder, AuthenticationUserRepository authenticationUserRepository, EmailService emailService){
        this.jsonWebToken = jsonWebToken;
        this.encoder = encoder;
        this.authenticationUserRepository = authenticationUserRepository;
        this.emailService = emailService;
    }
 

/**
    * Generates a random numeric email verification token consisting of 5 digits.
    * <p>
    * This method uses a cryptographically secure random number generator
    * ({@link java.security.SecureRandom}) to ensure that each token is unpredictable
    * and suitable for use in verification processes such as email confirmation codes.
    * </p>
    *
     * @return a randomly generated 5-digit numeric verification code as a {@link String}
     */
    public static String generateEmailVerificationToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            token.append(random.nextInt(10));
        }
        return token.toString();
    }


    /**
     *     Generates a random 5-digit numeric token, hashes it, stores it with expiry,
     *     and sends an email to the user for verification.
     * @param email the email address of the user to send the verification token to
     * @throws IllegalArgumentException if the user is not found or already verified
     */
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

    System.out.println("Generated token for " + email + ": " + emailVerificationToken); //  debug

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





    /**
     *     Validates the email verification token, checks expiry, and marks email as verified.
     * @param token the plain-text verification token entered by the user
     * @param email the email address associated with the verification attempt
     * @throws IllegalArgumentException if the user does not exist, token is invalid, or expired
     */
public void validateEmailVerificationToken(String token, String email) {
    
    // Node 1 : Return user using email
    Optional<AuthenticationUser> userOpt = authenticationUserRepository.findByEmail(email);
    // Node 2: Check if user exists
    if (userOpt.isEmpty()) {
        throw new IllegalArgumentException("User not found.");
    }
    // Node 3: Extract user and print info
    AuthenticationUser user = userOpt.get();
    System.out.println("Submitted token: " + token);
    System.out.println("Saved hashed token: " + user.getEmailVerificationToken());
    // Node 4: Check if provided token matches hashed token
    if (encoder.matches(token, user.getEmailVerificationToken())) {       
        // Node 5: Check if token is expired
        if (user.getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Email verification token expired.");
        }
        // Node 6: Mark email as verified
        user.setEmailVerified(true);
        // Node 7: Clear verification token + expiry
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiryDate(null);
        // Node 8: Save updated user
        authenticationUserRepository.save(user);
        // Node 9: Print success message
        System.out.println("Email verified for: " + email);
    } 
    // Node 10: Token does NOT match
    else {
        throw new IllegalArgumentException("Invalid email verification token.");
    }
}







    /**
     *     Retrieves the AuthenticationUser from the repository.
     * @param email the email address of the user to retrieve
     * @return the {@link AuthenticationUser} matching the provided email
     * @throws IllegalArgumentException if no user is found with the given email
     */
    public AuthenticationUser getUser(String email){
        return authenticationUserRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public AuthenticationUser getUserWithId(long id){
        return authenticationUserRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    /**
     *     Registers a new user, encodes their password, generates an email verification token,
     *     sends the verification email, and returns a JWT in the AuthenticationResponseBody.
     * @param registerRequestBody a DTO containing user registration details
     * @return an {@link AuthenticationResponseBody} containing the JWT and a confirmation message
     */
public AuthenticationResponseBody register(AuthenticationRequestBody registerRequestBody) {
    // Save new user with name, email, and encoded password
    AuthenticationUser user = authenticationUserRepository.save(
            new AuthenticationUser(
                    registerRequestBody.getName(), // 👈 added name
                    registerRequestBody.getLastName(),
                    registerRequestBody.getUsername(),
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




    /**
     * Generates a password reset token and sends it to the user's email.
     * @param email the email address of the user requesting a password reset
     * @throws IllegalArgumentException if the user does not exist
     */
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


    /**
     * Validates the reset token, updates the user's password, and clears token fields.
     * @param email       the user's email address
     * @param newPassword the new password to be set
     * @param token       the plain-text reset token provided by the user
     * @throws IllegalArgumentException if token validation fails or is expired
     */
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




    /**
    *     Authenticates a user by verifying email and password, marks them as online,
    *     and returns a JWT in the AuthenticationResponseBody.
     * @param loginRequestBody a DTO containing the user’s login credentials
     * @return an {@link AuthenticationResponseBody} containing JWT, username, email, and login status
     * @throws IllegalArgumentException if credentials are invalid
     */
    public AuthenticationResponseBody login(AuthenticationRequestBody loginRequestBody) {
        
        // Node 1: Find user by email
        AuthenticationUser user = authenticationUserRepository
        .findByEmail(loginRequestBody.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("User not found."));
       // Node 2: exit (user not found)
       

        // Node 3: Validate password
        if (!encoder.matches(loginRequestBody.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Password is incorrect.");
        }

        // Node 4: Mark user as online
        user.setLoginStatus(true);

        // Node 5: Save updated user
        authenticationUserRepository.save(user);

        // Node 6: Generate JWT token
        String token = jsonWebToken.generateToken(loginRequestBody.getEmail());
        
        // Node 7: Return response body
        return new AuthenticationResponseBody(token, "Authentication succeeded. ", 
        user.getUsername(), 
        user.getEmail(), 
        user.isLoginStatus());
    }



/**
 *   logout(String token):
 *     Marks the user associated with the JWT as offline.
     * @param token the JWT token of the user to log out
     * @throws IllegalArgumentException if no user corresponds to the provided token
     */
public void logout(String token) {
    String email = jsonWebToken.getEmailFromToken(token);
    AuthenticationUser user = authenticationUserRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found."));

    user.setLoginStatus(false);
    authenticationUserRepository.save(user);
}

/**
 *  Returns a list of all currently logged-in users.
     * @return a {@link List} of {@link AuthenticationUser} objects representing online users
     */
public List<AuthenticationUser> getOnlineUsers() {
    return authenticationUserRepository.findByLoginStatusTrue();
}

public AuthenticationUser updateUserProfile(Long userId, String firstName, String lastName, String position, String location){
    AuthenticationUser user = authenticationUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

    if(firstName != null) user.setName(firstName);
    if(lastName != null) user.setLastName(lastName);
    if(position != null) user.setPosition(position);
    if(location != null) user.setLocation(location);

    return authenticationUserRepository.save(user);
}

@Transactional
public void deleteUser(Long userId) {
    AuthenticationUser user = entityManager.find(AuthenticationUser.class, userId);
    if (user != null) {
        entityManager.createNativeQuery("DELETE FROM posts_likes WHERE user_id = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
        authenticationUserRepository.deleteById(userId);
    }
}




}
