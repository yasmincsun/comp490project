/**
 * Class Name: TicketMasterAPI
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 * Description:
 * This class connects the application to the Ticketmaster API. It sends HTTP
 * requests to retrieve event data based on a keyword search and returns the
 * results to the service layer.
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
package com.musicApp.backend.map.TicketMasterAPI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.musicApp.backend.map.TicketMaster.Dates.Status;
import com.musicApp.backend.map.dto.EventDTO;
import com.musicApp.backend.map.dto.TicketmasterResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
     * @param keyword the search word used to find events
     * @return a {@link Flux} of {@link EventDTO} objects containing matching event data
     */
    public Flux<EventDTO> getAllEvents(String keyword) {
        return fetchPage(keyword, 0)
                .flatMapMany(firstResponse -> {
                    if (firstResponse == null || firstResponse.getPages() == null) {
                        return Flux.empty();
                    }

                    int totalPages = firstResponse.getPages().getTotalPages();

                    return Flux.range(0, totalPages)
                            .delayElements(java.time.Duration.ofMillis(600))
                            .concatMap(page -> getEvents(keyword, page));
                });
    }

    /**
     * Retrieves a single page of event data from the Ticketmaster API.
     *
     * @param keyword the search word used to find events
     * @param page the page number to retrieve
     * @return a {@link Mono} containing the {@link TicketmasterResponse} for the requested page
     */
    private Mono<TicketmasterResponse> fetchPage(String keyword, int page) {
        return webclient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/events.json")
                        .queryParam("keyword", keyword)
                        .queryParam("apikey", apiKey)
                        .queryParam("locale", "*")
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .bodyToMono(TicketmasterResponse.class);
    }

    /**
     * Retrieves and filters events from a specific page.
     *
     * @param keyword the search word used to find events
     * @param page the page number to retrieve
     * @return a {@link Flux} of {@link EventDTO} objects that are on sale and contain valid URLs
     */
    public Flux<EventDTO> getEvents(String keyword, int page) {
        return fetchPage(keyword, page)
                .flatMapMany(response -> {
                    if (response == null ||
                            response.getEmbedded() == null ||
                            response.getEmbedded().getEvents() == null) {
                        return Flux.empty();
                    }

                    return Flux.fromIterable(response.getEmbedded().getEvents())
                            .filter(event ->
                                    event.getDates() != null &&
                                    event.getDates().getStatus() != null &&
                                    event.getDates().getStatus().getCode() == Status.ONSALE &&
                                    event.getUrl() != null
                            );
                });
    }
}