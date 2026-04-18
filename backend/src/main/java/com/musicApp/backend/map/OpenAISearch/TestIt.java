package com.musicApp.backend.map.OpenAISearch;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musicApp.backend.map.dto.EventDTO;
import com.musicApp.backend.spotify.MoodService;

import com.musicApp.backend.map.OpenAISearch.ConcertSearch;
import com.musicApp.backend.map.OpenAISearch.OpenAISearchConcerts;
import com.musicApp.backend.map.OpenAISearch.ConcertSearch;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * A REST controller to test out the AI search function for finding concerts
 */
@RestController
public class TestIt {

  /**
   * Initalizes services to use the AI searches
   */
  private final OpenAISearchConcerts x;
  private final ConcertSearch y;

  /**
   * A constructor that is linked to {@link MoodService}
   * @param x holds the services to OpenAISearchConcerts
   * @param y holds the services to ConcertSearch 
   */
  public TestIt(OpenAISearchConcerts x, ConcertSearch y) { this.x = x; this.y = y;}


  /**
   * A simple test to get the results of the AI to reach the front end
   * @return a Flux object, which is a JSON that holds concert/event details such as artist, genre, venue, address, and description
   * @throws Exception if method is missing like a prompt or the results of searchForConcerts
   */
  @GetMapping("/peekaboo")
  public Flux<EventDTO> test() throws Exception{
    System.out.println("PIIING");
    String ping = x.searchForConcerts("I want to see concerts like Post Malone");

    Flux results = y.searchConcerts(ping);

    return results;
  }
}