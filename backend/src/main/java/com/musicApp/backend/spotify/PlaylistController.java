package com.musicApp.backend.spotify;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import se.michaelthelin.spotify.SpotifyApi;

import java.util.*;

@RestController
public class PlaylistController {
  private final SpotifyAuthService auth;
  private final MoodService moodService;

  public PlaylistController(SpotifyAuthService auth, MoodService moodService) {
    this.auth = auth;
    this.moodService = moodService;
  }

  /** 
   * Create a new private playlist and add provided track URIs.
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
   * One-call endpoint: build a playlist from mood results and save to the user's Spotify.
   * Allows both GET and POST so you can trigger from browser or API client.
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
          .description("Auto-generated from mood: " + res.mood().bucket())
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