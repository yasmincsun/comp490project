package com.musicApp.backend.spotify;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
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
/**
 * REST controller for Spotify search integration.
 * Exposes a search endpoint that queries the Spotify API for tracks, artists, or albums.
 * Converts Spotify API payloads into lightweight JSON objects for frontend consumption.
 * @author Yasmin Zubair
 * Date: April 15th, 2026
 */
public class SpotifySearchController {

    private final SpotifyAuthService spotifyAuthService;

    /**
     * Create a SpotifySearchController with the given auth service.
     * @param spotifyAuthService service used to obtain authenticated Spotify API instances
     */
    public SpotifySearchController(SpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
    }

    /**
     * Search Spotify for tracks, artists, or albums.
     * @param query search query string
     * @param type search type, one of track, artist, or album
     * @param limit maximum number of results to return
     * @return response entity containing search results or an error payload
     */
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

            if ("album".equals(type)) {
                Paging<AlbumSimplified> payload = spotifyApi.searchAlbums(query).limit(limit).build().execute();
                List<Map<String, Object>> items = Arrays.stream(payload.getItems()).map(album -> {
                    return Map.of(
                        "id", album.getId(),
                        "name", album.getName(),
                        "artists", Arrays.stream(album.getArtists()).map(artist -> artist.getName()).collect(Collectors.toList()),
                        "images", Arrays.stream(album.getImages()).map(img -> Map.of("url", img.getUrl(), "width", img.getWidth(), "height", img.getHeight())).collect(Collectors.toList()),
                        "release_date", album.getReleaseDate()
                    );
                }).collect(Collectors.toList());

                Map<String, Object> response = new HashMap<>();
                response.put("albums", Map.of("items", items));
                response.put("query", query);
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.badRequest().body(Map.of("error", "Unsupported type (track|artist|album)"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Spotify search failed", "detail", e.getMessage()));
        }
    }
}
