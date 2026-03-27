package com.musicApp.backend.map.TicketMasterAPI;

import java.util.List;

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

  public TicketMasterAPI(WebClient.Builder builder, @Value("${ticketmaster.apiKey}") String apiKey){
    this.apiKey = apiKey;
    this.webclient = builder.baseUrl("https://app.ticketmaster.com/discovery/v2").build();
  }

public Flux<EventDTO> getAllEvents(String keyword) {
  
    return fetchPage(keyword, 0)
        .flatMapMany(firstResponse -> {
            if (firstResponse == null || firstResponse.getPages() == null) {
                return Flux.empty();
            }

            int totalPages = firstResponse.getPages().getTotalPages();

            return Flux.range(0, totalPages)
                .concatMap(page -> getEvents(keyword, page));
        });
}

private Mono<TicketmasterResponse> fetchPage(String keyword, int page) {
    return webclient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/events.json")
            .queryParam("keyword", keyword)
            .queryParam("apikey", apiKey)
            .queryParam("locale", "*")
            .queryParam("page", page)
            .build()
        )
        .retrieve()
        .bodyToMono(TicketmasterResponse.class);
}

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

