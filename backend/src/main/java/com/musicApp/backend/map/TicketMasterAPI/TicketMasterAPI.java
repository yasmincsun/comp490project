/**
 * Class Name: TicketMasterAPI
 * Date: February 13, 2026
 * @author Jose Bastidas 
 *
 */
package com.musicApp.backend.map.TicketMasterAPI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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
 * number of pages available. It then retrieves the events from the first page,
 * followed by the remaining pages. Events are filtered so only those with valid
 * URLs and usable venue location data are returned.
 */
@Component
public class TicketMasterAPI {
    private final WebClient webClient;
    private final String apiKey;

    /**
     * Creates a TicketMasterAPI object with a WebClient and API key.
     *
     * @param builder the WebClient builder used to configure the API client
     * @param apiKey the API key used to access the Ticketmaster API
     */
    public TicketMasterAPI(WebClient.Builder builder, @Value("${ticketmaster.apiKey}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = builder.baseUrl("https://app.ticketmaster.com/discovery/v2")
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
        return getAllEvents(keyword, null, null);
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

                    int maxPages = 20;
                    int pagesToFetch = Math.min(totalPages, maxPages);

                    Flux<EventDTO> firstPageEvents = extractEvents(firstResponse);

                    Flux<EventDTO> remainingPages =
                            pagesToFetch <= 1
                                    ? Flux.empty()
                                    : Flux.range(1, pagesToFetch - 1)
                                            .delayElements(java.time.Duration.ofMillis(600))
                                            .concatMap(page ->
                                                    getEvents(keyword, lat, lng, page)
                                                            .onErrorResume(error -> {
                                                                System.err.println("Failed to fetch page " + page + ": " + error.getMessage());
                                                                return Flux.empty();
                                                            })
                                            );

                    return firstPageEvents
                            .concatWith(remainingPages)
                            .distinct(EventDTO::getId);
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
        return webClient.get()
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
     * @return a {@link Flux} of {@link EventDTO} objects that contain valid URLs and venue coordinates
     */
    public Flux<EventDTO> getEvents(String keyword, Double lat, Double lng, int page) {
        return fetchPage(keyword, lat, lng, page)
                .flatMapMany(this::extractEvents);
    }

    /**
     * Extracts and filters events from a Ticketmaster response.
     *
     * @param response the Ticketmaster API response containing event data
     * @return a {@link Flux} of {@link EventDTO} objects that contain valid URLs
     *         and venue coordinates
     */
    private Flux<EventDTO> extractEvents(TicketmasterResponse response) {
        if (response == null ||
                response.getEmbedded() == null ||
                response.getEmbedded().getEvents() == null) {
            return Flux.empty();
        }

        return Flux.fromIterable(response.getEmbedded().getEvents())
                .filter(event -> event.getUrl() != null && !event.getUrl().isBlank())
                .filter(event ->
                        event.getEmbedded() != null &&
                        event.getEmbedded().getVenues() != null &&
                        event.getEmbedded().getVenues().length > 0 &&
                        event.getEmbedded().getVenues()[0] != null &&
                        event.getEmbedded().getVenues()[0].getLocation() != null &&
                        event.getEmbedded().getVenues()[0].getLocation().getLatitude() != null &&
                        event.getEmbedded().getVenues()[0].getLocation().getLongitude() != null
                );
    }
}