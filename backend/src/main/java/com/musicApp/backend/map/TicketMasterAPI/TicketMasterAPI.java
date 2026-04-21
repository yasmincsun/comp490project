/**
 * Class Name: TicketMasterAPI
 * Date: February 13, 2026
 * @author Jose Bastidas 
 *
 */package com.musicApp.backend.map.TicketMasterAPI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.musicApp.backend.map.TicketMaster.Dates.Status;
import com.musicApp.backend.map.dto.EventDTO;
import com.musicApp.backend.map.dto.TicketmasterResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This class connects the application to the Ticketmaster API. It sends HTTP
 * requests to retrieve event data based on a keyword search and optional map
 * location, then returns the results to the service layer.
 *
 * Important Data Structures:
 * Uses Flux and Mono from Project Reactor to handle asynchronous event data.
 *
 * Algorithm:
 * This class first retrieves the first page of results to determine the total
 * number of pages available. It then loops through each page, retrieves the
 * events, and filters them so only events that are on sale and contain a valid
 * URL are returned. A delay is added between page requests to avoid sending too
 * many requests too quickly.
 */
@Component
public class TicketMasterAPI {
    private final WebClient webclient;
    private final String apiKey;

    /**
     * Creates a TicketMasterAPI object with a WebClient and API key.
     *
     * @param builder the WebClient builder used to configure the API client
     * @param apiKey the API key used to access the Ticketmaster API
     */
    public TicketMasterAPI(WebClient.Builder builder, @Value("${ticketmaster.apiKey}") String apiKey) {
        this.apiKey = apiKey;
        this.webclient = builder.baseUrl("https://app.ticketmaster.com/discovery/v2")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

    /**
     * Retrieves all event pages that match the given keyword.
     *
     * <p>This method is kept so other parts of the application, such as concert
     * searching, can continue to search by keyword only.
     *
     * @param keyword the search word used to find events
     * @return a {@link Flux} of {@link EventDTO} objects containing matching event data
     */
    public Flux<EventDTO> getAllEvents(String keyword) {
        return fetchPage(keyword, null, null, 0)
                .flatMapMany(firstResponse -> {
                    if (firstResponse == null || firstResponse.getPages() == null) {
                        return Flux.empty();
                    }

                    int totalPages = firstResponse.getPages().getTotalPages();

                    if (totalPages <= 0) {
                        return Flux.empty();
                    }

                    return Flux.range(0, totalPages)
                            .delayElements(java.time.Duration.ofMillis(600))
                            .concatMap(page -> getEvents(keyword, null, null, page));
                });
    }

    /**
     * Retrieves all event pages that match the given keyword and location.
     *
     * @param keyword the search word used to find events
     * @param lat the latitude used to search for nearby events
     * @param lng the longitude used to search for nearby events
     * @return a {@link Flux} of {@link EventDTO} objects containing matching event data
     */
    public Flux<EventDTO> getAllEvents(String keyword, Double lat, Double lng) {
        return fetchPage(keyword, lat, lng, 0)
                .flatMapMany(firstResponse -> {
                    if (firstResponse == null || firstResponse.getPages() == null) {
                        return Flux.empty();
                    }

                    int totalPages = firstResponse.getPages().getTotalPages();

                    if (totalPages <= 0) {
                        return Flux.empty();
                    }

                    return Flux.range(0, totalPages)
                            .delayElements(java.time.Duration.ofMillis(600))
                            .concatMap(page -> getEvents(keyword, lat, lng, page));
                });
    }

    /**
     * Retrieves a single page of event data from the Ticketmaster API.
     *
     * @param keyword the search word used to find events
     * @param lat the latitude used to search for nearby events
     * @param lng the longitude used to search for nearby events
     * @param page the page number to retrieve
     * @return a {@link Mono} containing the {@link TicketmasterResponse} for the requested page
     */
    private Mono<TicketmasterResponse> fetchPage(String keyword, Double lat, Double lng, int page) {
    return webclient.get()
            .uri(uriBuilder -> {
                var builder = uriBuilder
                        .path("/events.json")
                        .queryParam("apikey", apiKey)
                        .queryParam("locale", "*")
                        .queryParam("page", page)
                        .queryParam("size", 50);

                if (keyword != null && !keyword.isBlank()) {
                    builder.queryParam("keyword", keyword.trim());
                }

                if (lat != null && lng != null) {
                    builder.queryParam("latlong", lat + "," + lng)
                            .queryParam("radius", 200)
                            .queryParam("unit", "miles");
                }

                return builder.build();
            })
            .retrieve()
            .bodyToMono(TicketmasterResponse.class);
}

    /**
     * Retrieves and filters events from a specific page.
     *
     * @param keyword the search word used to find events
     * @param lat the latitude used to search for nearby events
     * @param lng the longitude used to search for nearby events
     * @param page the page number to retrieve
     * @return a {@link Flux} of {@link EventDTO} objects that are on sale and contain valid URLs
     */
    public Flux<EventDTO> getEvents(String keyword, Double lat, Double lng, int page) {
        return fetchPage(keyword, lat, lng, page)
                .flatMapMany(response -> {
                    if (response == null ||
                            response.getEmbedded() == null ||
                            response.getEmbedded().getEvents() == null) {
                        return Flux.empty();
                    }

                    return Flux.fromIterable(response.getEmbedded().getEvents())
                            .filter(event -> event.getUrl() != null)
                            .filter(event ->
                                    event.getEmbedded() != null &&
                                    event.getEmbedded().getVenues() != null &&
                                    event.getEmbedded().getVenues().length > 0 &&
                                    event.getEmbedded().getVenues()[0] != null &&
                                    event.getEmbedded().getVenues()[0].getLocation() != null &&
                                    event.getEmbedded().getVenues()[0].getLocation().getLatitude() != null &&
                                    event.getEmbedded().getVenues()[0].getLocation().getLongitude() != null
                            );
                });
    }
}