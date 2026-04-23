package com.musicApp.backend.map.TicketMaster.Country;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class represents country information for a venue or event location.
 * It stores the country name and country code returned from the API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country {
    private String name;
    private String countryCode;

    /**
     * Returns the country name.
     *
     * @return the country name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the country code.
     *
     * @return the country code
     */
    public String getCountryCode() {
        return countryCode;
    }
}