package com.musicApp.backend.spotify;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Storage for the user's tokens
 * 
 * The class holds the access token and refresh token for the authenticated user.
 * It allows for quick lookup and removal during a session
 */

public class AuthStore {

  /**
   * A record holding the access and refresh token, plus how long before expiration
   * @param accessToken is a temporary Spotify access token
   * @param refreshToken is used to refresh access token to prevent session from prematurely
   * ending
   * @param expiresAtEpochSec holds the seconds left before expiration
   */
  public record Tokens(String accessToken, String refreshToken, long expiresAtEpochSec) {}

  /**
   * An internal map storing user IDs and their tokens
   */
  private static final ConcurrentHashMap<String, Tokens> TOKENS = new ConcurrentHashMap<>();

  /**
   * Method to store or update a user's token info
   * @param userId is the User
   * @param t is both the access and refresh token
   */
  public static void put(String userId, Tokens t) { TOKENS.put(userId, t); }

  /**
   * Retrieves the token of a user
   * @param userId is the user
   * @return the Token of the user, but returns null if not found
   */
  public static Tokens get(String userId) { return TOKENS.get(userId); }

  /**
   * Removes a user's token data and logging them out
   * @param userId is the user
   */
  public static void remove(String userId) { TOKENS.remove(userId); }
}

