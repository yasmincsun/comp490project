package com.musicApp.backend.map.OpenAISearch;

import com.musicApp.backend.map.TicketMasterAPI.TicketMasterAPI;
import com.musicApp.backend.map.dto.EventDTO;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * This class handles AI-assisted concert searching.
 *
 * <p>It first sends the user's prompt to the AI service to determine a genre
 * and a list of similar artists. It then searches Ticketmaster for concerts
 * that match those artist names.
 */
@Service
public class ConcertSearch {
    /**
     * Holds access to the OpenAISearchConcerts file, including the main search method.
     */
    private final OpenAISearchConcerts x;

    /**
     * Holds access to the TicketMaster API.
     */
    private final TicketMasterAPI y;

    /**
     * A record that holds the AI's JSON result from SearchConcert method in OpenAISearchConcerts.
     */
    public record ConcertIntentDTO(
        String genre,
        List<String> similar_artists
    ) {}

    /**
     * A constructor to use OpenAISearchConcerts and the TicketMasterAPI.
     *
     * @param x holds OpenAISearchConcerts access
     * @param y holds access to the TicketMasterAPI
     */
    public ConcertSearch(OpenAISearchConcerts x, TicketMasterAPI y) {
        this.x = x;
        this.y = y;
    }

    /**
     * This method returns concert listings based on the user's natural-language prompt.
     *
     * <p>The prompt is first sent to the AI service, which returns JSON describing
     * a genre and similar artists. Those artist names are then used to search
     * Ticketmaster for matching concerts.
     *
     * @param prompt holds the user's request to look for concerts based on an artist or genre
     * @return a {@link Flux} of {@link EventDTO} objects containing concert data
     * @throws Exception throws an exception if the AI response cannot be parsed
     */
    public Flux<EventDTO> searchConcertsFromPrompt(String prompt) throws Exception {

        if (prompt == null || prompt.isBlank()) {
            return Flux.empty();
        }

        String aiJson = x.searchForConcerts(prompt);

        if (aiJson == null || aiJson.isBlank() || aiJson.equals("Prompt is too short.")) {
            return Flux.empty();
        }

        ObjectMapper mapping = new ObjectMapper();
        ConcertIntentDTO parsed = mapping.readValue(aiJson, ConcertIntentDTO.class);

        List<String> preferredArtists = parsed.similar_artists();

        if (preferredArtists == null || preferredArtists.isEmpty()) {
            return Flux.empty();
        }

        return Flux.fromIterable(preferredArtists)
                .map(String::trim)
                .filter(artist -> !artist.isBlank())
                .distinct()
                .delayElements(java.time.Duration.ofMillis(700))
                .concatMap(y::getAllEvents)
                .distinct(EventDTO::getId);
    }
}
