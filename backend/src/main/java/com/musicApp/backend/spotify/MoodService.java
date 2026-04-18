/**
 * Date: September 25, 2025
 * @author Allen Guevarra
 */
package com.musicApp.backend.spotify;

import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;

import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.model_objects.specification.Paging;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.openai.client.OpenAIClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputItem;
import com.openai.models.responses.ResponseOutputMessage;
import com.openai.models.responses.Tool;
import com.openai.models.responses.WebSearchTool;

import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A service that generates a suggestion of songs, based on the user's most listened artists. The improvement
 * being it utilizes OpenAI to make these suggestions instead of relying on artist tags
 */

@Service
public class MoodService {

  /**
   * In respective order, auth grants the user access to Spotify's services, client holds the
   * access to use OpenAI, and model holds the OpenAI model used to make song decisions
   */
  private final SpotifyAuthService auth;
  private final OpenAIClient client;
  private final String model;

  /**
   * A record to hold the object Song, which includes:
   * @param title (the title of song)
   * @param artist (the person/people that made the song)
   * @param confidence (this is a score, which decides whether or not a song fits a mood or not)
   */
  public record Song(String title, String artist, double confidence) {}

  /**
   * A record that holds an Artist's songs
   * 
   * @param number (Every artist is assigned a number based on most listened to least listened)
   * @param artist (the person or people that made these songs)
   * @param songs (a list of all the songs from the artist)
   */
  public record ArtistSongs(int number, String artist, List<String> songs) {}

  /**
   * A constructor that gives the user access to Spotify, as well as the OpenAI
   * @param auth (holds the access to Spotify)
   * @param model (holds the model that OpenAI uses)
   * @param client (holds the access to OpenAI)
   */
  public MoodService(SpotifyAuthService auth, @Value("${openai.model}") String model, OpenAIClient client) {
    this.auth = auth;
    this.client = client;
    this.model = model;
  }

  /**
   * Debugging purposes, used to time response time
   */

  long startTime;
  long endTime;


  /**
   * The actual "Mood Search Engine." What it does it collects all the artists the user has listened into a list, and put it in a pool of suggestions.
   * We then shuffle the order of artists and pick out the first 30. We convert the selected artists into a JSON format, to which we create a prompt for
   * the AI. We ask it to pick 10-20 artists from the 30 given and suggest 2-3 songs from each artist. We gave it parameters like no dupes, keep a
   * 60/40 ratio of popular songs to deep cuts, not to include remixes, remasters, or live versions, among more parameters. We also give it tags to
   * help the AI grasp the idea of the mood. The AI returns with a list of songs, along with a confidence score. It then goes into 2 final methods.
   * @param userId holds the user's Spotify ID
   * @param mood holds the desired mood the user wants
   * @param limit holds the max number of artists for consideration
   * @return returns a list of URIs, which basically are the final songs to give to the user
   * @throws Exception in the event the user is missing one of these parameters or have an invalid parameter
   */
  public List<String> recommendBySelectedMood(String userId, String mood, int limit) throws Exception {
    List<Integer> selectionSublist;
    List<Map<String, Object>> top;
    String prompt;

    startTime = System.currentTimeMillis();

    SpotifyApi api = auth.apiForUser(userId);

        Artist[] items = api.getUsersTopArtists().limit(limit).build().execute().getItems();

        if (items == null || items.length == 0){
          top = new ArrayList<>();
        } else {
          top = IntStream.range(0, items.length).mapToObj(i -> {Map<String, Object> map2 = new HashMap<>(); 
            map2.put("number", i + 1);
            map2.put("id", items[i].getId()); 
            map2.put("name", items[i].getName()); 
            return map2; }).collect(Collectors.toList());
        }

        Tool webSearch = Tool.ofWebSearch(
        WebSearchTool.builder()
        .type(WebSearchTool.Type.WEB_SEARCH)   // <-- required
        .build()
          );

        ObjectMapper mapper = new ObjectMapper();
        String artistsNames = mapper.writeValueAsString(top);

        System.out.println(artistsNames);

        List<Integer> sample = new ArrayList<>();

        while (sample.size() < Math.min(50, top.size())) {
          int randomNum = ThreadLocalRandom.current().nextInt(1, top.size() + 1);
          if (!sample.contains(randomNum)) {
            sample.add(randomNum);
          }
        }

        if (sample.size() >= 30){
          selectionSublist = sample.subList(0, 30);
        } else {
          selectionSublist = sample.subList(0, sample.size());
        }

        ObjectMapper selectMapper = new ObjectMapper();
        String selection = selectMapper.writeValueAsString(selectionSublist);

        System.out.println(selection);

        List<Map<String, Object>> selectedArtist = new ArrayList<>();

        for (Map<String, Object> artist : top){
          int number = (int) artist.get("number");
          if (selectionSublist.contains(number)){
            Map<String, Object> entry = new HashMap<>();
            entry.put("number", number);
            entry.put("artist", artist.get("name"));
            selectedArtist.add(entry);
          }
        }

        ObjectMapper selectedArtistMapper = new ObjectMapper();
        String selectedArtistString = selectedArtistMapper.writeValueAsString(selectedArtist);
    

        List<Pattern> tags = MoodMatchers.MOOD_MATCHERS.get(mood);

        List<String> tag2String = tags.stream().map(Pattern::pattern).toList();

        String tagString = tag2String.stream().collect(Collectors.joining(", "));

        endTime = System.currentTimeMillis();
        System.out.println("Time taken for Spotify response: " + (endTime - startTime) / 1000.0 + " seconds");

        if (sample.size() >= 30){

        prompt = String.format("""
                  You are generating Spotify Song candidates for a mood
                  Mood: %s
                  Allowed artists (choose from these that fit the mood): %s
                  Mood tags/defs: %s

                  Output: JSON array of 30 UNIQUE objects: {title, artist, confidence}
                  Constraints:
                  - artist MUST be in allowed list; no dups or your own suggestions
                  - use 10-20 of the 30 artists, 2-3 songs per artist, do not exceed more than 3 songs per artist; ~60%% popular / ~40%% deep cuts (not top-5 for artist)
                  - exclude remaster/remix/live/intro/skit; exclude collabs unless allowed artist is featured
                  - confidence is NUMBER 0-1; include only >= 0.75 (0.90-1.00 strong, 0.75-0.89 ok)
                  - Prefer song genres that match the mood strongly. For example if mood is "angry", prioritize aggressive/intense genres (metal/punk/hard rock/hardcore/rap) over pop/R&B.
                  - Add songs that match the mood instrumentally

                  Return JSON only.
                  """, mood, selectedArtistString, tagString);
        } else{ //in the case user is new and has less than 30 top artists, we will not give the AI the selection instructions and just let it choose from the artists it thinks fit best
          prompt = String.format("""
                    Mood: %s
                    Preferred artists: %s
                    Mood tags: %s

                    Output: JSON array of 30 UNIQUE objects: {title, artist, confidence} (NUMBER 0-1)
                    Rules:
                    - prioritize preferred artists; aim 2-3 songs/artist when possible; rank them higher
                    - if <30 artists, add similar artists (in terms of genre) to reach 30; tracks must be real + on Spotify
                    - for preferred artists, use titles from user dataset when possible
                    - ~60%% popular / ~40%% deep cuts; no remaster/remix/live/intro/skit; no collabs unless original artist is featured
                    - include only confidence >= 0.75 (0.90-1.00 strong, 0.75-0.89 ok)

                    Return JSON only.
                    """, mood, artistsNames, tagString);
        }

        startTime = System.currentTimeMillis();


        ResponseCreateParams params = ResponseCreateParams.builder()
                .model(model)
                .input(prompt)
                .tools(List.of(webSearch))
                .temperature(0.9)
                .topP(1.0)
                .build();
        
        Response res = client.responses().create(params);

        String aiResponse = extractText(res);

        System.out.println(aiResponse); 

        ObjectMapper songMapper = new ObjectMapper();

        List<Song> songs = songMapper.readValue(aiResponse, new com.fasterxml.jackson.core.type.TypeReference<List<Song>>(){});
        
        List<String> URIs = new ArrayList<>();

        endTime = System.currentTimeMillis();
        System.out.println("Time taken for AI response: " + (endTime - startTime) / 1000.0 + " seconds");

        if (sample.size() < 30){
          //if user has less than 30 artists, we will not give the AI the selection instructions and just let it choose from the artists it thinks fit best, so we will not filter the songs based on the selection and just take the top 20 songs that fit the mood
          return songCompilerPriority(URIs, songs, api, songs.size(), 0.75);
        } 
          return songCompiler(URIs, songs, api, sample.size(), 0.80);

  }

  /**
   * This method acts as a checkpoint for songs. This specific method has one use case: in the event the user has less than 30 listened artists. 
   * It basically checks if a song exists and not made up by AI, and if it does exist, it checks for
   * the song's confidence score to see if it fits the mood perfectly. If it reaches the minumum or surpasses the score, it heads into the final
   * playlist. The method also has a limit to how many songs can be in a playlist, which is 30.
   * The reason being that it's "songCompilerPriority" is because it
   * prioritize suggesting a user's listened to artist before the AI can use their own suggestions. The confidence score
   * is fixed to 0.75 just to set the bar low on what it can suggest, while using the user's listened artists, to which a backfill method suggests
   * songs from the AI and give out better song reccomendations
   * @param x holds an empty list that'll eventually be filled with Spotify URIs
   * @param y holds a list of songs the AI suggested
   * @param api holds the services for the SpotifyAPI
   * @param size holds the size of the song list
   * @param confidence holds the minimum score a song needs to advance
   * @return the final playlist
   * @throws Exception in the event a parameter is missing or is invalid
   */

  public static List<String> songCompilerPriority(List<String> x, List<Song> y, SpotifyApi api, int size, double confidence) throws Exception { //needs work

       System.out.println("MEthode Priority triggered");
       long startTime2 = System.currentTimeMillis();
       long endTime2;

       Collections.shuffle(y);

    for (Song prioritySongs : y) {
          SearchTracksRequest searchReq = api.searchTracks("track:" + prioritySongs.title + " artist:" + prioritySongs.artist).limit(1).build();
          
          Paging<Track> results = searchReq.execute();

          if (results.getItems().length == 0) {
            continue;
          }

          if (prioritySongs.confidence >= confidence && top.stream().anyMatch(artist -> artist.get("name").equals(prioritySongs.artist))){

            if (x.size() == 10){
              break;
            }

            Track song = results.getItems()[0];
            String uri = song.getUri();

            if (!x.contains(uri)) {
              x.add(uri);
              System.out.println("added: " + prioritySongs.title + " by " + prioritySongs.artist + " with confidence " + prioritySongs.confidence);
              System.out.println("Current size: " + x.size());
            }
          } else {
            continue;
          }

        }

        System.out.println("Backfilling");

        for (Song t : y){
          SearchTracksRequest searchReq = api.searchTracks("track:" + t.title + " artist:" + t.artist).limit(1).build();
          
          Paging<Track> results = searchReq.execute();

          if (results.getItems().length == 0) {
            continue;
          }

          if (t.confidence >= 0.80){
             Track song = results.getItems()[0];

             String uri = song.getUri();

            if (x.size() == 20){
              break;
            } else {
              if (!x.contains(uri)) {
              x.add(uri);
              System.out.println("added: " + t.title + " by " + t.artist + " with confidence " + t.confidence);
              System.out.println("Current size (Backfill): " + x.size());
              }
            }
          }
        }

        endTime2 = System.currentTimeMillis();
        System.out.println("Time taken for backfilling: " + (endTime2 - startTime2) / 1000.0 + " seconds");

        System.out.println("Recommended URIs: " + x);
        return x;
  }

  /**
   * This is ideally the default songCOmpiler method if you have 50 or more artists. It does the same as songCompilerPriority, but the difference is
   * that because a user has a bigger sample size of artist suggestions, the confidence score is hardcoded to 0.80 so the first
   * 15 suggestions fit the mood. After it gets all the songs
   * with that score, it backfills the playlist with songs that have a 0.75 score just to be lenient and more experimental.
   * @param x holds an empty list that'll eventually be filled with Spotify URIs
   * @param y holds a list of songs the AI suggested
   * @param api holds the services for the SpotifyAPI
   * @param size holds the size of the song list
   * @param confidence holds the minimum score a song needs to advance
   * @return the final playlist
   * @throws Exception in the event a parameter is missing or is invalid
   */

  public static List<String> songCompiler(List<String> x, List<Song> y, SpotifyApi api, int size, double confidence) throws Exception { //needs work

       System.out.println("MEthode songCompiler triggered");
       long startTime2 = System.currentTimeMillis();
       long endTime2;

       Collections.shuffle(y);

    for (Song s : y) {
          SearchTracksRequest searchReq = api.searchTracks("track:" + s.title + " artist:" + s.artist).limit(1).build();
          
          Paging<Track> results = searchReq.execute();

          if (results.getItems().length == 0) {
            continue;
          }

          if (s.confidence >= confidence){

            if (x.size() == 15){
              break;
            }

            Track song = results.getItems()[0];
            String uri = song.getUri();

            if (!x.contains(uri)) {
              x.add(uri);
              System.out.println("added: " + s.title + " by " + s.artist + " with confidence " + s.confidence);
              System.out.println("Current size: " + x.size());
            }
          } else {
            continue;
          }

        }

        System.out.println("Backfilling");

        for (Song t : y){
          SearchTracksRequest searchReq = api.searchTracks("track:" + t.title + " artist:" + t.artist).limit(1).build();
          
          Paging<Track> results = searchReq.execute();

          if (results.getItems().length == 0) {
            continue;
          }

          if (t.confidence >= 0.75){
             Track song = results.getItems()[0];

             String uri = song.getUri();

            if (x.size() == 20){
              break;
            } else {
              if (!x.contains(uri)) {
              x.add(uri);
              System.out.println("added: " + t.title + " by " + t.artist + " with confidence " + t.confidence);
              System.out.println("Current size (Backfill): " + x.size());
              }
            }
          }
        }

        endTime2 = System.currentTimeMillis();
        System.out.println("Time taken for backfilling: " + (endTime2 - startTime2) / 1000.0 + " seconds");

        System.out.println("Recommended URIs: " + x);
        return x;
  }

  /**
   * This method turns Json into a String
   * @param response is the Json being converted into a string
   * @return a string of the Json
   */

  public static String extractText(Response response) {
    if (response == null || response.output() == null) return null;

    for (ResponseOutputItem item : response.output()) {
      var msgOpt = item.message();
      if (msgOpt.isEmpty()) continue;

      var msg = msgOpt.get();
      if (msg.content() == null) continue;

      for (ResponseOutputMessage.Content c : msg.content()) {
        var outTextOpt = c.outputText();
        if (outTextOpt.isPresent()) {
          return outTextOpt.get().text();
        }
      }
    }
    return null;
  }
}