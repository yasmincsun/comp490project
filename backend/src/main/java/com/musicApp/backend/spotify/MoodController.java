package com.musicApp.backend.spotify;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.musicApp.backend.spotify.MoodService;
import com.musicApp.backend.spotify.MoodService.MoodResult;

import jakarta.servlet.http.HttpSession;

import java.util.Map;

/**
 * A REST controller for creating the music recommendations based on mood
 * 
 * It helps expose the endpoints that allow users to recieve recommendations based
 * on their desired mood (ex: happy, chill, pumped, nostalgic)
 * 
 * 
 */
@RestController
public class MoodController {


  /**
   * Helps compute mood vectors and generate reccomendations
   */
  private final MoodService mood;

  /**
   * A constructor that is linked to {@link MoodService}
   * @param mood the "search engine" used to analyze user's listening habits
   */
  public MoodController(MoodService mood) { this.mood = mood; }


  /**
   * The endpoint thatgets the music reccomendations for the user.
   * It calls {@link MoodService} and generates the recommendations. It's important
   * that the user is logged in and if not, it returns a 401 error, or unauthorized access
   * @param mood holds the desired mood the user feels
   * @param session the current session with the user's ID
   * @return a JSON the shows the recommendated music, an error message, or an exception
   */
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