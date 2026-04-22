package com.musicApp.backend.map.OpenAISearch;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musicApp.backend.map.dto.EventDTO;

import reactor.core.publisher.Flux;

/**
 * A REST controller to test out the AI search function for finding concerts.
 */
@RestController
public class TestIt {

    /**
     * Initializes services to use the AI searches.
     */
    private final OpenAISearchConcerts x;
    private final ConcertSearch y;

    /**
     * A constructor that is linked to the AI concert search services.
     *
     * @param x holds the services to OpenAISearchConcerts
     * @param y holds the services to ConcertSearch
     */
    public TestIt(OpenAISearchConcerts x, ConcertSearch y) {
        this.x = x;
        this.y = y;
    }

    /**
     * A simple test to get the results of the AI to reach the front end.
     *
     * @return a {@link Flux} object holding concert and event details
     * @throws Exception if the AI response cannot be parsed
     */
    @GetMapping("/peekaboo")
    public Flux<EventDTO> test() throws Exception {
        System.out.println("PIIING");
        return y.searchConcertsFromPrompt("I want to see concerts like Post Malone");
    }

    /**
     * A simple test endpoint to see the raw AI JSON response.
     *
     * @return the raw JSON string returned by the AI service
     */
    @GetMapping("/peekaboo-json")
    public String testJson() {
        return x.searchForConcerts("I want to see concerts like Post Malone");
    }
}