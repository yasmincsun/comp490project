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


@Service
public class MoodService {


  private final SpotifyAuthService auth;
  private final OpenAIClient client;
  private final String model;

  public record Song(String title, String artist, double confidence) {}
  public record ArtistSongs(int number, String artist, List<String> songs) {}


  public MoodService(SpotifyAuthService auth, @Value("${openai.model}") String model, OpenAIClient client) {
    this.auth = auth;
    this.client = client;
    this.model = model;
  }

  long startTime;
  long endTime;

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
          selectionSublist = sample.subList(0, 20);
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
          return songCompilerPriority(URIs, songs, api, songs.size(), 0.75, top);
        } 
          return songCompiler(URIs, songs, api, sample.size(), 0.80);

  }

  public static List<String> songCompilerPriority(List<String> x, List<Song> y, SpotifyApi api, int size, double confidence, List<Map<String, Object>> top) throws Exception { //needs work

       System.out.println("MEthode Priority triggered");
       long startTime2 = System.currentTimeMillis();
       long endTime2;

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

  public static List<String> songCompiler(List<String> x, List<Song> y, SpotifyApi api, int size, double confidence) throws Exception { //needs work

       System.out.println("MEthode songCompiler triggered");
       long startTime2 = System.currentTimeMillis();
       long endTime2;

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