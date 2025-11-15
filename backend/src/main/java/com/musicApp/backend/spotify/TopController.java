package com.musicApp.backend.spotify;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Artist;

import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A REST controller to show a user's top artists
 */

@RestController
public class TopController {

  /**
   * Handles Spotify Authentication and give the API calls
   */
  private final SpotifyAuthService auth;

    /**
     * A constructor with the authentication service
     * @param auth holds the Spotify authentication service to allow access to the user's top artist
     */
  public TopController(SpotifyAuthService auth) { this.auth = auth; }

    /**
     * An endpoint that shows the user's top Spotify Artist
     * @param session holds the current HTTP session
     * @return a JSON of the User ID and a lost of their top artists
     * @throws Exception if the request fails
     */
  @GetMapping("/me/top")
  public ResponseEntity<?> top(HttpSession session) throws Exception {
    String userId = (String) session.getAttribute("userId");
    if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));

    SpotifyApi api = auth.apiForUser(userId);
    Artist[] items = api.getUsersTopArtists().limit(50).build().execute().getItems();
    List<Map<String, Object>> top = Arrays.stream(items)
        .map(a -> Map.<String,Object>of("id", a.getId(), "name", a.getName()))
        .toList();
    return ResponseEntity.ok(Map.of("userId", userId, "topArtists", top));
  }
}