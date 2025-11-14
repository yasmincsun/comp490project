/**
 * Date: September 25, 2025
 * @author Jose Bastidas
 */

package com.musicApp.backend.features.authentication.utils;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class responsible for encoding strings (like passwords or tokens)
 * using SHA-256 hashing and Base64 encoding. Provides a secure way to store
 * sensitive data such as passwords, email verification tokens, and password reset tokens.
 * 
 */
@Component
public class Encoder {

    /**
     *     Converts a plain string into a SHA-256 hash and encodes it in Base64.
     * @param rawString the plain text string to encode (e.g., password or token)
     * @return a {@link String} representing the Base64-encoded SHA-256 hash
     * @throws RuntimeException if the SHA-256 algorithm is not available
     */
    public String encode(String rawString){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawString.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error encoding string", e);
        }
    }

    /**
     * Checks whether the raw string, when encoded, matches the provided encoded string.
     * @param rawString     the plain text string to check
     * @param encodedString the previously encoded Base64-encoded SHA-256 hash
     * @return {@code true} if the encoded raw string matches the provided encoded string;
     *         {@code false} otherwise
     */
    public boolean matches(String rawString, String encodedString){
        return encode(rawString).equals(encodedString);
    }
}
