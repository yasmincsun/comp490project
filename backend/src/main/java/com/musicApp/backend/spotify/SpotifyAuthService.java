package com.musicApp.backend.spotify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;
import java.util.UUID;

/**
 * This file handles authentication, token management, and creating authorized sessions for users
 */

@Service
public class SpotifyAuthService {

    /**
     * Logs authentication events
     */
  private static final Logger log = LoggerFactory.getLogger(SpotifyAuthService.class);

  /**
   * Both client Id and client Secret hold credentials to use the API
   */
  private final String clientId;
  private final String clientSecret;

  /**
   * The redirect URI for performing callbacks
   */
  private final URI redirectUri;

  /**
   * An API instance to build authorization URLs
   */
  private final SpotifyApi baseApi;


  /**
   * A constructor that builds the authentication service
   * @param redirectUri holds the redirect URI url, 
   * @param clientId holds an ID to use the API services
   * @param clientSecret holds a secret value to secure API use
   * @throws IllegalStateException in case clientsecret or clientID aren't available or correct
   */
  public SpotifyAuthService(
      @Value("${app.redirectUri}") String redirectUri,
      @Value("${spotify.client-id}") String clientId,
      @Value("${spotify.client-secret}") String clientSecret
  ) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.redirectUri = SpotifyHttpManager.makeUri(redirectUri);
    log.info("Using redirect URI: {}", this.redirectUri);

    if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
      throw new IllegalStateException("SPOTIFY_CLIENT_ID / SPOTIFY_CLIENT_SECRET are not set");
    }

    // Base API used for building the login URL (no user tokens set on this)
    this.baseApi = new SpotifyApi.Builder()
        .setClientId(this.clientId)
        .setClientSecret(this.clientSecret)
        .setRedirectUri(this.redirectUri)
        .build();
  }
  /**
   * Builds the Spotify login URL and allow the user to use the search engine
   * @param state a random string to prevent CSRF attacks
   * @return the url for Spotify authorization
   */
  public String buildLoginUrl(String state) {
    AuthorizationCodeUriRequest req = baseApi.authorizationCodeUri()
        .scope("user-top-read playlist-modify-private")
        .state(state)
        .show_dialog(true)
        .build();
    return req.execute().toString();
  }

  /**
   * Exchanges authorization code for access and refresh tokens
   * @param code is the code recieved from Spotify's callback (or redirect URI)
   * @return the {@link LoginResult} with the tokens and a new user ID
   * @throws Exception if there is a failure
   */
  public LoginResult exchangeCode(String code) throws Exception {
    // Exchange code for tokens
    AuthorizationCodeRequest tokenReq = baseApi.authorizationCode(code).build();
    AuthorizationCodeCredentials creds = tokenReq.execute();

    // Use a fresh per-user client (avoid mutating baseApi)
    SpotifyApi userApi = new SpotifyApi.Builder()
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setRedirectUri(redirectUri)
        .build();
    userApi.setAccessToken(creds.getAccessToken());
    userApi.setRefreshToken(creds.getRefreshToken());

    // Identify the user
    User me = userApi.getCurrentUsersProfile().build().execute();
    String userId = me.getId();

    // Persist tokens with expiry
    long expiresAt = Instant.now().getEpochSecond() + creds.getExpiresIn();
    AuthStore.put(userId, new AuthStore.Tokens(
        creds.getAccessToken(), creds.getRefreshToken(), expiresAt
    ));

    return new LoginResult(userId, creds.getAccessToken(), creds.getRefreshToken(), expiresAt);
  }

  /**
   * Gives te user an authorized {@link SpotifyApi} instance for the user
   * @param userId holds the user's Spotify ID
   * @return a client with the API accesses
   * @throws Exception if a user isnt logged in
   */

  public SpotifyApi apiForUser(String userId) throws Exception {
    AuthStore.Tokens t = AuthStore.get(userId);
    if (t == null) throw new IllegalStateException("User not logged in");

    // Build a *fresh* instance for this user (do not reuse or mutate baseApi)
    SpotifyApi api = new SpotifyApi.Builder()
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setRedirectUri(redirectUri)
        .build();
    api.setAccessToken(t.accessToken());
    api.setRefreshToken(t.refreshToken());


    //Refresher
    long now = Instant.now().getEpochSecond();
    if (t.expiresAtEpochSec() - now < 60) {
      AuthorizationCodeRefreshRequest refreshReq = api.authorizationCodeRefresh().build();
      AuthorizationCodeCredentials refreshed = refreshReq.execute();
      long newExp = Instant.now().getEpochSecond() + refreshed.getExpiresIn();
      AuthStore.put(userId, new AuthStore.Tokens(refreshed.getAccessToken(), t.refreshToken(), newExp));
      api.setAccessToken(refreshed.getAccessToken());
    }

    return api;
  }
  /**
   * Generates the state for requests to prevent CSRF attacks
   * @return a random string
   */
  public String newState() { return UUID.randomUUID().toString(); }

  /**
   * A record of spotify logins and tokens
   * @param userId holds the user's Spotify ID
   * @param accessToken holds the user's access Token
   * @param refreshToken holds a refresh token to ensure sessions dont expire
   * @param expiresAtSec holds time before access token expires
   */
  public record LoginResult(String userId, String accessToken, String refreshToken, long expiresAtSec) {}
}
