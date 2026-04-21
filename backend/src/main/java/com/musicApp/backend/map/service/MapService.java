/**
 * Class Name: MapService
 * Date: February 13, 2026
 * @author Jose Bastidas 
 *
 */
package com.musicApp.backend.map.service;

import org.springframework.stereotype.Service;

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

    /**
     * Creates a MapService object with access to the TicketMaster API.
     *
     * @param ticketMasterAPI the API service used to retrieve event data
     */
    public MapService(TicketMasterAPI ticketMasterAPI) {
        this.ticketmasterAPI = ticketMasterAPI;
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
}