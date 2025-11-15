package com.musicApp.backend.spotify;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class AuthController {

    /**
   * Tracks any errors or authentication events
   */
  private static final Logger log = LoggerFactory.getLogger(AuthController.class);

  /**
   * Does Spotify authentication and exchange tokens
   */
  private final SpotifyAuthService auth;

    /**
   * A constructor that is liked with the SpotifyAPI dependency
   * 
   * @param auth helps handle OAuth logic for Spotify
   */
  public AuthController(SpotifyAuthService auth) {
    this.auth = auth;
  }

  /**
   * This method initiates Spotify OAuth login process by creating a token and storing it in the session, 
   * then taking them to a permission page
   * 
   * @param session the current HTTP session used to store the OAuth state
   * @return redirects a response to Spotify's login page, or shows any error that occured
   */
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


/**
   * Helps validate the state parameter, echange the authorization code
   * for an access toke, and stores the user ID in the session
   * 
   * If anything where to fail, such as missing code, invalid state, or an error
   * in Spotify itself, the error is returned
   * 
   * @param code the authorization code returned by Spotify
   * @param state the OAuth value for validation
   * @param errorParam error message returned by Spotify
   * @param session the HTTP session used to retrieve and store the user's data
   * @param resp the servlet response object
   * @return a redirect to "me/top" route as a sign of success, or an error response
   */
@GetMapping("/callback")
public ResponseEntity<?> callback(
        @RequestParam(name = "code", required = false) String code,
        @RequestParam(name = "state", required = false) String state,
        @RequestParam(name = "error", required = false) String errorParam,
        HttpSession session
) {
    try {
        log.info("Received /callback request with code: {}, state: {}, error: {}", code, state, errorParam);

        // Check if Spotify returned an error
        if (errorParam != null) {
            log.warn("Spotify returned error: {}", errorParam);
            return ResponseEntity.status(400).body(Map.of("error", errorParam));
        }

        // Ensure code is present
        if (code == null || code.isBlank()) {
            log.error("Missing 'code' parameter in /callback");
            return ResponseEntity.status(400).body(Map.of("error", "Missing authorization code"));
        }

        // Validate state
        String expectedState = (String) session.getAttribute("oauth_state");
        session.removeAttribute("oauth_state");
        if (expectedState != null && state != null && !expectedState.equals(state)) {
            log.error("Invalid state parameter. Expected: {}, Got: {}", expectedState, state);
            return ResponseEntity.status(400).body(Map.of("error", "Invalid state"));
        }

        // Attempt to exchange the code for an access token
        SpotifyAuthService.LoginResult result;
        try {
            log.info("Exchanging authorization code with Spotify...");
            result = auth.exchangeCode(code);
            log.info("Exchange successful. User ID: {}", result.userId());
        } catch (Exception e) {
            log.error("Spotify code exchange failed", e);
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Spotify code exchange failed",
                    "detail", e.getMessage()
            ));
        }

        // Store user info in session
        session.setAttribute("userId", result.userId());
        log.info("User session updated with userId: {}", result.userId());

        // Redirect to /me/top
        return ResponseEntity.status(302).location(URI.create("/me/top")).build();

    } catch (Exception e) {
        log.error("Unexpected callback error", e);
        return ResponseEntity.status(500).body(Map.of(
                "error", "Unexpected server error",
                "detail", e.getMessage()
        ));
    }
}

  /**
   * Logs out the user and removes their credentials from {@link AuthStore}
   * @param session holds the current HTTP session containing user data
   * @return a JSON message that user has logged out successfully
   */
  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpSession session) {
    String userId = (String) session.getAttribute("userId");
    if (userId != null) AuthStore.remove(userId);
    session.invalidate();
    return ResponseEntity.ok(Map.of("message", "Logged out"));
  }
}
