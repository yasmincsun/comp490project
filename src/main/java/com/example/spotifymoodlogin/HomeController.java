package com.example.spotifymoodlogin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
  private final SpotifyAuthService auth;
  public HomeController(SpotifyAuthService auth) { this.auth = auth; }

  @GetMapping("/")
  public String home() {
    return "Page is running on 127.0.0.1:8080";
  }
}
