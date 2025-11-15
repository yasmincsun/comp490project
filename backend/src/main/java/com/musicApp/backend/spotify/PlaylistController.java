/**
 * Date: September 25, 2025
 * @author Allen Guevarra
 */
package com.musicApp.backend.spotify;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import se.michaelthelin.spotify.SpotifyApi;

import java.util.*;

/**
 * 
 * A REST controller that handles the creation of the playlist
 * 
 * It contains endpoints to create playlists, add tracks to those playlists, and generate them
 * 
 */
@RestController
public class PlaylistController {

  /**
   * Gives Spotify authentication to the user
   */
  private final SpotifyAuthService auth;

  /**
   * The service that generates the recommendations
   */
  private final MoodService moodService;

    /**
     * A constructor that creates the authentication and mood service
     * 
     * @param auth is the Spotify authentication
     * @param moodService is the service for the mood search engine
     */
  public PlaylistController(SpotifyAuthService auth, MoodService moodService) {
    this.auth = auth;
    this.moodService = moodService;
  }

  /**
   * Create a new private playlist 
   * 
   * @param body is the JSON that contains the playlist details and track URIs
   * @param session is the current HTTP session
   * @return a {@link ResponseEntity} with the playlist or with an error message
   * 
   */
  @PostMapping("/playlist")
  public ResponseEntity<?> createAndAdd(@RequestBody Map<String, Object> body, HttpSession session) {
    try {
      String userId = (String) session.getAttribute("userId");
      if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));

      String name = String.valueOf(body.getOrDefault("name", "My Mood Playlist"));
      @SuppressWarnings("unchecked")
      List<String> uris = (List<String>) body.getOrDefault("uris", List.of());
      if (uris.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "No track URIs provided"));

      SpotifyApi api = auth.apiForUser(userId);
      var created = api.createPlaylist(userId, name).public_(false).build().execute();

      for (int i = 0; i < uris.size(); i += 100) {
        String[] chunk = uris.subList(i, Math.min(i + 100, uris.size())).toArray(new String[0]);
        api.addItemsToPlaylist(created.getId(), chunk).build().execute();
      }

      return ResponseEntity.ok(Map.of(
          "playlistId", created.getId(),
          "name", created.getName(),
          "tracksAdded", uris.size(),
          "url", created.getExternalUrls() != null ? created.getExternalUrls().get("spotify") : null
      ));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }

  /**
   * Adds track(s) to a playlist
   * 
   * @param body contains a JSON of the Playlist ID and URIs to add
   * @param session the current HTTP session 
   * @return a {@link ResponseEntity} with the tracks added or an error
   */

  /** Add items to an existing playlist by ID. */
  @PostMapping("/playlist/add")
  public ResponseEntity<?> addToExisting(@RequestBody Map<String, Object> body, HttpSession session) {
    try {
      String userId = (String) session.getAttribute("userId");
      if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));

      String playlistId = String.valueOf(body.get("playlistId"));
      if (playlistId == null || playlistId.isBlank()) return ResponseEntity.badRequest().body(Map.of("error", "playlistId required"));

      @SuppressWarnings("unchecked")
      List<String> uris = (List<String>) body.getOrDefault("uris", List.of());
      if (uris.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "No track URIs provided"));

      SpotifyApi api = auth.apiForUser(userId);
      for (int i = 0; i < uris.size(); i += 100) {
        String[] chunk = uris.subList(i, Math.min(i + 100, uris.size())).toArray(new String[0]);
        api.addItemsToPlaylist(playlistId, chunk).build().execute();
      }

      return ResponseEntity.ok(Map.of("playlistId", playlistId, "tracksAdded", uris.size()));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }

  /**
   * Builds a playlist based on the mood preference
   * 
   * @param mood is the user's selected mood
   * @param name is the custom playlist name
   * @param limit is the number of songs allowed in a playlist
   * @param session the current HTTP session
   * @return a {@link ResponseEntity} containing the playlist generated, or an error
   */
  @RequestMapping(value = "/playlist/from-mood", method = { RequestMethod.GET, RequestMethod.POST })
  public ResponseEntity<?> playlistFromMood(
      @RequestParam(name = "mood", required = false) String mood,
      @RequestParam(name = "name", required = false) String name,
      @RequestParam(name = "limit", required = false) Integer limit,
      HttpSession session
  ) {
    try {
      String userId = (String) session.getAttribute("userId");
      if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));

      int useLimit = (limit == null) ? 20 : Math.max(1, Math.min(100, limit));

      var res = moodService.recommendBySelectedMood(userId, mood, useLimit);
      var recs = res.recommendations();
      if (recs == null || recs.isEmpty()) {
        return ResponseEntity.badRequest().body(Map.of("error", "No tracks found for mood", "mood", mood));
      }

      List<String> uris = new ArrayList<>();
      for (var m : recs) {
        Object uri = m.get("uri");
        if (uri != null) uris.add(uri.toString());
      }
      if (uris.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "No track URIs in recommendations"));

      SpotifyApi api = auth.apiForUser(userId);
      String playlistName = (name == null || name.isBlank())
          ? ("Mood • " + res.mood().bucket())
          : name.trim();

      var created = api.createPlaylist(userId, playlistName)
          .public_(false)
          .description("Here's a playlist when you feel " + res.mood().bucket())
          .build()
          .execute();

      for (int i = 0; i < uris.size(); i += 100) {
        String[] chunk = uris.subList(i, Math.min(i + 100, uris.size())).toArray(new String[0]);
        api.addItemsToPlaylist(created.getId(), chunk).build().execute();
      }

      return ResponseEntity.ok(Map.of(
          "playlistId", created.getId(),
          "name", created.getName(),
          "mood", res.mood().bucket(),
          "tracksAdded", uris.size(),
          "url", created.getExternalUrls() != null ? created.getExternalUrls().get("spotify") : null
      ));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }
}