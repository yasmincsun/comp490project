package com.musicApp.backend.map.OpenAISearch;

import com.musicApp.backend.map.OpenAISearch.OpenAISearchConcerts;
import com.musicApp.backend.map.TicketMasterAPI.TicketMasterAPI;
import com.musicApp.backend.spotify.MoodService;

import com.musicApp.backend.map.TicketMaster.Dates.Status;
import com.musicApp.backend.map.dto.EventDTO;
import com.musicApp.backend.map.dto.TicketmasterResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ConcertSearch {
    /**
   * Holds access to the OpenAISearchConcerts file, including the main search method
   */
  private final OpenAISearchConcerts x;

  /**
   * Holds access to the TicketMaster API
   */

  private final TicketMasterAPI y;

  /**
   * A record that holds the AI's JSON result from SearchConcert method in OpenAISearchConcerts
   */
  public record ConcertIntentDTO(
    String genre,
    List<String> similar_artists
) {}

/**
 * A constructor to use OpenAISearchConcerts and the TicketMasterAPI
 * @param x holds OpenAISearchConcerts access
 * @param y holds access to the TicketMasterAPI
 */
  public ConcertSearch(OpenAISearchConcerts x, TicketMasterAPI y) { this.x = x; this.y = y; }

  /**
   * This method returns concert listings based on the AI results
   * @param res holds the user's request to look for concerts based on an artist
   * @throws Exception throws an exception if there's something wrong with user's prompt
   */
  public Flux<EventDTO> searchConcerts(String res ) throws Exception {

    if (res == null || res.isBlank()){
        return Flux.empty();
    }

    ObjectMapper mapping = new ObjectMapper();

    ConcertIntentDTO parsed = mapping.readValue(res, ConcertIntentDTO.class);

    List<String> preferredArtists = parsed.similar_artists();

    return Flux.fromIterable(preferredArtists).delayElements(java.time.Duration.ofMillis(700))
        .map(String::trim)
        .filter(artist -> !artist.isBlank())
        .distinct()
        .concatMap(y::getAllEvents);

  }

  
}
