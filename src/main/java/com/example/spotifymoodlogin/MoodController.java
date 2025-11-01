package com.example.spotifymoodlogin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

@RestController
public class MoodController {

  private final MoodService mood;

  public MoodController(MoodService mood) { this.mood = mood; }

  @GetMapping("/mood/by")
public ResponseEntity<?> byMood(
    @RequestParam(name = "mood", required = false) String mood,
    HttpSession session
) {
  try {
    String userId = (String) session.getAttribute("userId");
    if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
    var res = this.mood.recommendBySelectedMood(userId, mood);
    return ResponseEntity.ok(Map.of(
        "mood", Map.of(
            "valence", res.mood().valence(),
            "energy", res.mood().energy(),
            "tempo", res.mood().tempo(),
            "danceability", res.mood().danceability(),
            "bucket", res.mood().bucket()
        ),
        "recommendations", res.recommendations()
    ));
  } catch (Exception e) {
    return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
  }
}

}
