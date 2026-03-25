package com.musicApp.backend.spotify;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/spotify")
public class SpotifySearchController {

    private final SpotifyAuthService spotifyAuthService;

    public SpotifySearchController(SpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(name = "q") String query,
                                    @RequestParam(name = "type", defaultValue = "track") String type,
                                    @RequestParam(name = "limit", defaultValue = "20") int limit) {
        try {
            if (query == null || query.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Query text is required"));
            }

            SpotifyApi spotifyApi = spotifyAuthService.apiForClientCredentials();
            type = type.toLowerCase();

            if ("track".equals(type)) {
                Paging<Track> payload = spotifyApi.searchTracks(query).limit(limit).build().execute();
                List<Map<String, Object>> items = Arrays.stream(payload.getItems()).map(track -> {
                    return Map.of(
                        "id", track.getId(),
                        "name", track.getName(),
                        "artists", Arrays.stream(track.getArtists()).map(a -> a.getName()).collect(Collectors.toList()),
                        "album", Map.of(
                            "name", track.getAlbum() != null ? track.getAlbum().getName() : "",
                            "images", track.getAlbum() != null ? Arrays.stream(track.getAlbum().getImages()).map(img -> Map.of("url", img.getUrl(), "width", img.getWidth(), "height", img.getHeight())).collect(Collectors.toList()) : List.of()
                        ),
                        "uri", track.getUri(),
                        "duration_ms", track.getDurationMs()
                    );
                }).collect(Collectors.toList());

                Map<String, Object> response = new HashMap<>();
                response.put("tracks", Map.of("items", items));
                response.put("query", query);
                return ResponseEntity.ok(response);
            }

            if ("artist".equals(type)) {
                Paging<Artist> payload = spotifyApi.searchArtists(query).limit(limit).build().execute();
                List<Map<String, Object>> items = Arrays.stream(payload.getItems()).map(artist -> {
                    return Map.of(
                        "id", artist.getId(),
                        "name", artist.getName(),
                        "genres", artist.getGenres(),
                        "followers", artist.getFollowers() != null ? artist.getFollowers().getTotal() : 0,
                        "images", Arrays.stream(artist.getImages()).map(img -> Map.of("url", img.getUrl(), "width", img.getWidth(), "height", img.getHeight())).collect(Collectors.toList())
                    );
                }).collect(Collectors.toList());

                Map<String, Object> response = new HashMap<>();
                response.put("artists", Map.of("items", items));
                response.put("query", query);
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.badRequest().body(Map.of("error", "Unsupported type (track|artist)"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Spotify search failed", "detail", e.getMessage()));
        }
    }
}
