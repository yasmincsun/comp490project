/**
 * Class Name: MapService
 * Date: February 13, 2026
 * @author Jose Bastidas 
 *
 */package com.musicApp.backend.map.service;

import org.springframework.stereotype.Service;

import com.musicApp.backend.map.OpenAISearch.ConcertSearch;
import com.musicApp.backend.map.TicketMasterAPI.TicketMasterAPI;
import com.musicApp.backend.map.dto.EventDTO;

import reactor.core.publisher.Flux;

/**
 * This class provides map-related services for the application.
 * It communicates with the TicketMasterAPI class to retrieve
 * event data based on a search keyword and optional map location.
 */
@Service
public class MapService {
    private final TicketMasterAPI ticketmasterAPI;
    private final ConcertSearch concertSearch;

    /**
     * Creates a MapService object with access to the TicketMaster API
     * and the AI concert search service.
     *
     * @param ticketMasterAPI the API service used to retrieve event data
     * @param concertSearch the AI concert search service
     */
    public MapService(TicketMasterAPI ticketMasterAPI, ConcertSearch concertSearch) {
        this.ticketmasterAPI = ticketMasterAPI;
        this.concertSearch = concertSearch;
    }

    /**
     * Returns a list of events that match the given keyword and optional location.
     *
     * @param keyword the search word used to find events
     * @param lat the optional latitude used to search for nearby events
     * @param lng the optional longitude used to search for nearby events
     * @return a {@link Flux} of {@link EventDTO} objects containing event data
     */
    public Flux<EventDTO> getEventDTOs(String keyword, Double lat, Double lng) {
        return ticketmasterAPI.getAllEvents(keyword, lat, lng);
    }

    /**
     * Returns a list of events based on the user's natural-language AI prompt.
     *
     * @param prompt the user's request for AI-assisted concert searching
     * @return a {@link Flux} of {@link EventDTO} objects containing event data
     * @throws Exception throws an exception if the AI response cannot be parsed
     */
    public Flux<EventDTO> getAIEventDTOs(String prompt) throws Exception {
        return concertSearch.searchConcertsFromPrompt(prompt);
    }
}