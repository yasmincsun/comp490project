/**
 * Class Name: MapService
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 * Description:
 * This class provides map-related services for the application.
 * It communicates with the TicketMasterAPI class to retrieve
 * event data based on a search keyword.
 */
package com.musicApp.backend.map.service;

import org.springframework.stereotype.Service;

import com.musicApp.backend.map.TicketMasterAPI.TicketMasterAPI;
import com.musicApp.backend.map.dto.EventDTO;

import reactor.core.publisher.Flux;

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
     * Returns a list of events that match the given keyword.
     *
     * @param keyword the search word used to find matching events
     * @return a {@link Flux} of {@link EventDTO} objects containing event data
     */
    public Flux<EventDTO> getEventDTOs(String keyword) {
        return ticketmasterAPI.getAllEvents(keyword);
    }
}

