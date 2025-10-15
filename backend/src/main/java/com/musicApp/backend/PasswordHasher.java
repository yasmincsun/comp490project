package com.musicApp.backend;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class provides a method to hash passwords using SHA-256.
 * This ensures passwords are stored securely and not in plain text.
 */
public class PasswordHasher {

    public static String hash(String password) {
        try {
            // Create a SHA-256 message digest instance
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Convert password to bytes and gets the hash
            byte[] hashBytes = md.digest(password.getBytes());

            // Convert hash bytes to hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b)); // format as two-digit hex
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found");
        }
    }
}
