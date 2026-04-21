/**
 * Date: February 13, 2026
 * @author Jose Bastidas
 */
package com.musicApp.backend.map.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.musicApp.backend.map.dto.EventDTO;
import com.musicApp.backend.map.service.MapService;

import reactor.core.publisher.Flux;

/**
 * Handles map-related API requests for searching events.
 *
 * <p>This controller exposes an endpoint that allows clients to search for
 * events by keyword and optional location. The request is delegated to the
 * {@link MapService}, which returns matching {@link EventDTO} objects as a
 * reactive stream.
 */
@CrossOrigin(origins = {
    "http://127.0.0.1:5173",
    "http://localhost:5173"
})
@RestController
@RequestMapping("/api/v1/map")
public class MapController {

    /** Service used to retrieve event data for map-related requests. */
    private final MapService mapService;

    /**
     * Creates a new {@code MapController} with the required map service.
     *
     * @param mapService the service used to search and retrieve event data
     */
    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    /**
     * Searches for events that match the provided keyword and optional location.
     *
     * @param lat the optional latitude used to search for nearby events
     * @param lng the optional longitude used to search for nearby events
     * @param keyword the optional search term used to filter matching events
     * @return a {@link Flux} stream containing matching {@link EventDTO} objects
     */
    @GetMapping("/search")
    public Flux<EventDTO> listOfEvents(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false, defaultValue = "") String keyword) {
        return mapService.getEventDTOs(keyword, lat, lng);
    }
}