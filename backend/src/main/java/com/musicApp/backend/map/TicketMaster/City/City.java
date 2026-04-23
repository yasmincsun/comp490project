package com.musicApp.backend.map.TicketMaster.City;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class represents city information for a venue or event location.
 * It stores the city name returned from the API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class City {
    private String name;

    /**
     * Returns the city name.
     *
     * @return the city name
     */
    public String getName() {
        return name;
    }
}