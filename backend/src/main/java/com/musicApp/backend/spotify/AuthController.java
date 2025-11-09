package com.musicApp.backend.spotify;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// import com.musicApp.backend.spotify.AuthStore;
// import com.musicApp.backend.spotify.SpotifyAuthService;
// import com.musicApp.backend.spotify.SpotifyAuthService.LoginResult;

import java.net.URI;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class AuthController {

  private static final Logger log = LoggerFactory.getLogger(AuthController.class);
  private final SpotifyAuthService auth;

  public AuthController(SpotifyAuthService auth) {
    this.auth = auth;
  }

  @GetMapping("/login")
public ResponseEntity<?> login(HttpSession session) {
  try {
    String state = auth.newState();
    session.setAttribute("oauth_state", state);

    String url = auth.buildLoginUrl(state);
    // (Optional) log the redirect target for debugging
    // log.info("Redirecting to Spotify: {}", url);

    return ResponseEntity.status(302).location(URI.create(url)).build();
  } catch (Exception e) {
    log.error("Login failed", e);
    return ResponseEntity.internalServerError().body(Map.of(
        "error", "Login failed",
        "detail", e.getMessage()
    ));
  }
}


  @GetMapping("/callback")
public ResponseEntity<?> callback(
    @RequestParam(name = "code", required = false) String code,
    @RequestParam(name = "state", required = false) String state,
    @RequestParam(name = "error", required = false) String errorParam,
    HttpSession session,
    HttpServletResponse resp
) {
    try {
      if (errorParam != null) {
        log.warn("Spotify returned error param: {}", errorParam);
        return ResponseEntity.status(400).body(Map.of("error", errorParam));
      }
      if (code == null || code.isBlank()) {
        log.error("Missing 'code' on /callback");
        return ResponseEntity.status(400).body(Map.of("error", "Missing authorization code"));
      }

      String expected = (String) session.getAttribute("oauth_state");
      session.removeAttribute("oauth_state");
      if (expected != null && state != null && !expected.equals(state)) {
        log.error("Invalid state. expected={}, got={}", expected, state);
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid state"));
      }

      SpotifyAuthService.LoginResult result = auth.exchangeCode(code);
      session.setAttribute("userId", result.userId());
      return ResponseEntity.status(302).location(URI.create("/me/top")).build();
    } catch (Exception e) {
      log.error("Callback error", e);
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpSession session) {
    String userId = (String) session.getAttribute("userId");
    if (userId != null) AuthStore.remove(userId);
    session.invalidate();
    return ResponseEntity.ok(Map.of("message", "Logged out"));
  }
}
