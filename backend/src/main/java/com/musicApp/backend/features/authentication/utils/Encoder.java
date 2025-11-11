/**
 * Class Name: Encoder
 * Package: com.musicApp.backend.features.authentication.utils
 * Date: November 10, 2025
 * @author Jose Bastidas
 *
 *
 * Data Structures:
 * - byte[] for storing SHA-256 hash
 * - Base64 String for storing the encoded hash
 *
 * Algorithms / Design Decisions:
 * - SHA-256 hashing algorithm is used for secure one-way encryption.
 * - Base64 encoding ensures the hashed byte array can be safely stored as a String.
 * - matches() method provides a secure comparison without storing or revealing raw passwords.
 * - Chosen for security, simplicity, and compatibility with token/password verification.
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
     *     Inputs: rawString - the string to encode
     *     Outputs: Base64-encoded SHA-256 hash as a String
     * @param rawString
     * @return
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
     *     Inputs: rawString, encodedString
     *     Outputs: boolean indicating if the strings match
     * @param rawString
     * @param encodedString
     * @return
     */
    public boolean matches(String rawString, String encodedString){
        return encode(rawString).equals(encodedString);
    }
}
