package com.example.spotifymoodlogin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Artist;

import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class TopController {
  private final SpotifyAuthService auth;

  public TopController(SpotifyAuthService auth) { this.auth = auth; }

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
