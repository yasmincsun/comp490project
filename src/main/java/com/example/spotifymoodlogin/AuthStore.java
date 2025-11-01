package com.example.spotifymoodlogin;

import java.util.concurrent.ConcurrentHashMap;

public class AuthStore {
  public record Tokens(String accessToken, String refreshToken, long expiresAtEpochSec) {}

  private static final ConcurrentHashMap<String, Tokens> TOKENS = new ConcurrentHashMap<>();

  public static void put(String userId, Tokens t) { TOKENS.put(userId, t); }
  public static Tokens get(String userId) { return TOKENS.get(userId); }
  public static void remove(String userId) { TOKENS.remove(userId); }
}
