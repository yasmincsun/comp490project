package com.musicApp.backend.spotify;

import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Track;
import com.neovisionaries.i18n.CountryCode;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MoodService {

  private static final Pattern FEAT_PATTERN = Pattern.compile(
    "\\s*(\\(|\\[)?\\s*(feat\\.|featuring|with)\\s+[^)\\]]+(\\)|\\])?",
    Pattern.CASE_INSENSITIVE
  );

  private static final Pattern VERSION_PATTERN = Pattern.compile(
    "\\s*[-–:]?\\s*(remaster(ed)?(\\s*\\d{4})?|radio edit|single version|album version|clean|explicit|demo|live|mix|edit|version)\\b.*",
    Pattern.CASE_INSENSITIVE
  );

  private static String canonicalTitle(String name) {
  if (name == null) return "";
  String n = Normalizer.normalize(name, Normalizer.Form.NFKD);
  n = n.replaceAll("[\\p{M}]", "");             // strip accents
  n = FEAT_PATTERN.matcher(n).replaceAll("");   // drop "feat./featuring/with …"
  n = n.replaceAll("\\s*\\(.*?\\)\\s*$", "");   // trim trailing (…) blobs
  n = VERSION_PATTERN.matcher(n).replaceAll(""); // drop “remastered / edit / version …”
  n = n.replace("&", "and");
  n = n.replaceAll("[^a-z0-9]+", " ").trim().toLowerCase(); // normalize whitespace/case
  return n;
}

private static String primaryArtistId(Track t) {
  if (t == null || t.getArtists() == null || t.getArtists().length == 0 || t.getArtists()[0] == null)
    return "";
  return t.getArtists()[0].getId() == null ? "" : t.getArtists()[0].getId();
}

  private final SpotifyAuthService auth;

  public MoodService(SpotifyAuthService auth) {
    this.auth = auth;
  }

  public static record MoodVector(double valence, double energy, double tempo,
                                  double danceability, String bucket) {}
  public static record MoodResult(MoodVector mood,
                                  List<Map<String, Object>> recommendations) {}

  public MoodResult computeAndRecommend(String userId) throws Exception {
    SpotifyApi api = auth.apiForUser(userId);

    // 1) Primary: user's top tracks
    List<Track> pool = new ArrayList<>();
    try {
      // Rotate time_range and offset to avoid repetition 
    String[] ranges = {"short_term", "medium_term", "long_term"};
    String timeRange = ranges[(int)(System.currentTimeMillis() / 86_400_000L) % ranges.length]; // rotates daily
    int offset = new java.util.Random().nextInt(30); // vary results slightly

    Track[] topTracks = api.getUsersTopTracks()
    .time_range(timeRange)
    .limit(20)
    .offset(offset)
    .build()
    .execute()
    .getItems();

      if (topTracks != null) pool.addAll(Arrays.asList(topTracks));
    } catch (Exception ignored) { /* fallback below */ }

    // 2) Fallback: a few top tracks from each top artist (skip artists that error)
    if (pool.isEmpty()) {
      Artist[] topArtists = api.getUsersTopArtists().limit(5).build().execute().getItems();
      for (Artist a : topArtists) {
        try {
          Track[] artistTop = api.getArtistsTopTracks(a.getId(), CountryCode.US).build().execute();
          int limit = Math.min(5, artistTop.length);
          for (int i = 0; i < limit; i++) pool.add(artistTop[i]);
        } catch (Exception ignored) { /* regional/other issues → skip */ }
      }
    }

    if (pool.isEmpty()) {
      return new MoodResult(
          new MoodVector(0.5, 0.5, 120, 0.5, "unavailable_due_to_spotify_api_changes"),
          List.of()
      );
    }

    // 3) De-dupe and add variety
    LinkedHashMap<String, Track> unique = new LinkedHashMap<>();
    for (Track t : pool) {
      if (t != null && t.getId() != null){
        continue;
      }
      String key = canonicalTitle(t.getName()) + "::" + primaryArtistId(t);
      unique.putIfAbsent(key, t);
    }
    List<Track> uniq = new ArrayList<>(unique.values());
    Collections.shuffle(uniq, new SecureRandom()); // non-deterministic each call

    // 4) Present up to 20 items as "recommendations"
    List<Map<String, Object>> recList = uniq.stream()
        .limit(10)
        .map(t -> Map.<String, Object>of(
            "id", t.getId(),
            "name", t.getName(),
            "artist", (t.getArtists() != null && t.getArtists().length > 0)
                ? t.getArtists()[0].getName() : "Unknown",
            "uri", t.getUri()
        ))
        .collect(Collectors.toList());

    // Neutral mood vector
    MoodVector mv = new MoodVector(0.5, 0.5, 120, 0.5, "unavailable_due_to_spotify_api_changes");
    return new MoodResult(mv, recList);
  }

  /**
   * Genre-based filtering for a SELECTED mood (works in dev mode) with
   * non-deterministic, weighted sampling so each request can yield a new set.
   *
   * Example moods: happy, chill, pumped, melancholic
   */
  public MoodResult recommendBySelectedMood(String userId, String selectedMood) throws Exception {
    return recommendBySelectedMood(userId, selectedMood, 10);
  }

  public MoodResult recommendBySelectedMood(String userId, String selectedMood, int limit) throws Exception {
    SpotifyApi api = auth.apiForUser(userId);

    // ---------- Build the pool ----------
    List<Track> pool = new ArrayList<>();
    try {
      // Rotate time_range and offset to avoid repetition (3A)
    String[] ranges = {"short_term", "medium_term", "long_term"};
    String timeRange = ranges[(int)(System.currentTimeMillis() / 86_400_000L) % ranges.length];
    int offset = new java.util.Random().nextInt(30);

    Track[] topTracks = api.getUsersTopTracks()
    .time_range(timeRange)
    .limit(20)
    .offset(offset)
    .build()
    .execute()
    .getItems();

      if (topTracks != null) pool.addAll(Arrays.asList(topTracks));
    } catch (Exception ignored) {}

    if (pool.isEmpty()) {
      Artist[] topArtists = api.getUsersTopArtists().limit(50).build().execute().getItems();
      for (Artist a : topArtists) {
        try {
          Track[] artistTop = api.getArtistsTopTracks(a.getId(), CountryCode.US).build().execute();
          for (int i = 0; i < Math.min(5, artistTop.length); i++) pool.add(artistTop[i]);
        } catch (Exception ignored) {}
      }
    }

    if (pool.isEmpty()) {
      return new MoodResult(new MoodVector(0.5, 0.5, 120, 0.5, "no_data"), List.of());
    }

    // ---------- Build artist genre map for ALL artists on the tracks ----------
    LinkedHashSet<String> artistIds = new LinkedHashSet<>();
    for (Track t : pool) {
      if (t.getArtists() != null) {
        for (var art : t.getArtists()) {
          if (art != null && art.getId() != null) artistIds.add(art.getId());
        }
      }
    }

    Map<String, List<String>> artistGenres = new HashMap<>();
    List<String> ids = new ArrayList<>(artistIds);
    for (int i = 0; i < ids.size(); i += 50) {
      String[] chunk = ids.subList(i, Math.min(i + 50, ids.size())).toArray(new String[0]);
      Artist[] arts = api.getSeveralArtists(chunk).build().execute();
      if (arts != null) {
        for (Artist a : arts) {
          if (a != null && a.getId() != null && a.getGenres() != null) {
            artistGenres.put(a.getId(), Arrays.asList(a.getGenres()));
          }
        }
      }
    }

    Map<String, List<Pattern>> moodHash = MoodMatchers.MOOD_MATCHERS;
    String key = (selectedMood == null || selectedMood.isBlank()) ? "happy" : selectedMood.toLowerCase().trim();
    List<Pattern> patterns = moodHash.getOrDefault(key, moodHash.get("happy"));

    // ---------- Score tracks by ANY artist’s genres ----------
    record Scored(Track t, int score) {}
    List<Scored> scored = new ArrayList<>();
    for (Track t : pool) {
      int score = 0;
      if (t.getArtists() != null) {
        for (var art : t.getArtists()) {
          if (art == null || art.getId() == null) continue;
          List<String> genres = artistGenres.getOrDefault(art.getId(), List.of());
          for (String g : genres) {
            if (g == null) continue;
            String gl = g.toLowerCase();
            for (Pattern p : patterns) {
              if (p.matcher(gl).find()) { score++; break; } // count each artist once
            }
          }
        }
      }
      scored.add(new Scored(t, score));
    }

    // ---------- Prefer positives; if none exist, use all ----------
    List<Scored> candidates = scored.stream().filter(s -> s.score > 0).toList();
    if (candidates.isEmpty()) candidates = scored;

    // ---------- De-dupe by track ID ----------
    LinkedHashMap<String, Scored> uniqMap = new LinkedHashMap<>();
    for (Scored s : candidates) {
      if (s.t != null && s.t.getId() != null){
        continue;
      }
      String dedupeKey = canonicalTitle(s.t.getName()) + "::" + primaryArtistId(s.t);
      uniqMap.putIfAbsent(dedupeKey, s);
    }
    List<Scored> uniq = new ArrayList<>(uniqMap.values());

    // ---------- Group by artist; select NUM_ARTISTS artists; pick SONGS_PER_ARTIST per artist ----------
Random rnd = new SecureRandom();

// Build a canonical "primary artist" (first listed) for grouping
Map<String, List<Scored>> byArtist = new LinkedHashMap<>();
for (Scored s : candidates) {
  String artistId = null;
  if (s.t != null && s.t.getArtists()!=null && s.t.getArtists().length>0 && s.t.getArtists()[0]!=null) {
    artistId = s.t.getArtists()[0].getId();
  }
  if (artistId == null) continue;
  byArtist.computeIfAbsent(artistId, k -> new ArrayList<>()).add(s);
}

// Shuffle tracks inside each artist, but prefer higher score (tie-broken randomly)
for (List<Scored> list : byArtist.values()) {
  list.sort((a,b) -> Integer.compare(b.score, a.score));
  Collections.shuffle(list, rnd); // add stochasticity among similarly scored items
}

// Rank artists by their best track score (desc), tie-broken randomly
List<Map.Entry<String, List<Scored>>> artistEntries = new ArrayList<>(byArtist.entrySet());
artistEntries.sort((a,b) -> {
  int bestA = a.getValue().stream().mapToInt(x -> x.score).max().orElse(0);
  int bestB = b.getValue().stream().mapToInt(x -> x.score).max().orElse(0);
  int cmp = Integer.compare(bestB, bestA);
  return (cmp != 0) ? cmp : rnd.nextInt(3)-1; // weak random tie-break
});

// Pick up to NUM_ARTISTS artists
List<Map.Entry<String, List<Scored>>> pickedArtists = new ArrayList<>();
for (var e : artistEntries) {
  pickedArtists.add(e);
  if (pickedArtists.size() >= 10) break;
}

// If not enough artists matched the mood, backfill from all pool artists
if (pickedArtists.size() < 10) {
  // Build artist groups from the whole pool (ignoring mood), unique by ID
  LinkedHashMap<String, List<Scored>> allByArtist = new LinkedHashMap<>();
  LinkedHashMap<String, Track> uniqPool = new LinkedHashMap<>();
  for (Track t : pool) if (t != null && t.getId()!=null) uniqPool.putIfAbsent(t.getId(), t);
  for (Track t : uniqPool.values()) {
    if (t.getArtists()!=null && t.getArtists().length>0 && t.getArtists()[0]!=null) {
      String aid = t.getArtists()[0].getId();
      if (aid==null) continue;
      allByArtist.computeIfAbsent(aid, k -> new ArrayList<>()).add(new Scored(t, 0));
    }
  }
  // Don’t duplicate artists already picked
  Set<String> already = pickedArtists.stream().map(Map.Entry::getKey).collect(java.util.stream.Collectors.toSet());
  for (var e : allByArtist.entrySet()) {
    if (already.contains(e.getKey())) continue;
    pickedArtists.add(e);
    if (pickedArtists.size() >= 10) break;
  }
}

// Now pick up to SONGS_PER_ARTIST tracks per selected artist (unique by track ID)
LinkedHashSet<String> seenTrackIds = new LinkedHashSet<>();
List<Scored> chosen = new ArrayList<>();
for (var e : pickedArtists) {
  int taken = 0;
  // Prefer mood candidates first (they’re already in e.getValue())
  for (Scored s : e.getValue()) {
    if (s.t==null || s.t.getId()==null) continue;
    if (seenTrackIds.add(s.t.getId())) {
      chosen.add(s);
      taken++;
      if (taken >= 2) break;
    }
  }
  // If this artist had < SONGS_PER_ARTIST mood tracks, try to top up from the full pool for that artist
  if (taken < 2) {
    // collect more tracks by this artist from the full pool
    for (Track t : pool) {
      if (t==null || t.getId()==null || t.getArtists()==null || t.getArtists().length==0 || t.getArtists()[0]==null) continue;
      String aid = t.getArtists()[0].getId();
      if (aid!=null && aid.equals(e.getKey())) {
        if (seenTrackIds.add(t.getId())) {
          chosen.add(new Scored(t, 0));
          taken++;
          if (taken >= 2) break;
        }
      }
    }
  }
}

// As a final backfill (rare), if total < PLAYLIST_SIZE, add any remaining unique tracks
if (chosen.size() < 10) {
  LinkedHashMap<String, Track> allUniq = new LinkedHashMap<>();
  for (Track t : pool) if (t != null && t.getId()!=null) allUniq.putIfAbsent(t.getId(), t);
  for (Track t : allUniq.values()) {
    if (t==null || t.getId()==null) continue;
    long countForArtist = chosen.stream().filter(s ->
      s.t!=null && s.t.getArtists()!=null && s.t.getArtists().length>0 && s.t.getArtists()[0]!=null &&
      t.getArtists()!=null && t.getArtists().length>0 && t.getArtists()[0]!=null &&
      Objects.equals(s.t.getArtists()[0].getId(), t.getArtists()[0].getId())
    ).count();
    if (countForArtist >= 2) continue; // respect the cap
    if (seenTrackIds.add(t.getId())) {
      chosen.add(new Scored(t, 0));
      if (chosen.size() >= 10) break;
    }
  }
}

    // ---------- Build response ----------
    List<Map<String, Object>> recList = chosen.stream()
        .map(s -> Map.<String, Object>of(
            "id", s.t.getId(),
            "name", s.t.getName(),
            "artist", (s.t.getArtists()!=null && s.t.getArtists().length>0) ? s.t.getArtists()[0].getName() : "Unknown",
            "uri", s.t.getUri()
        ))
        .collect(Collectors.toList());

    // Neutral vector; bucket echoes the selected mood
    MoodVector mv = new MoodVector(0.5, 0.5, 120, 0.5, key);
    return new MoodResult(mv, recList);
  }

}