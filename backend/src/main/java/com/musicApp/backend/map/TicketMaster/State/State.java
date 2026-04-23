package com.musicApp.backend.map.TicketMaster.State;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class represents state information for a venue or event location.
 * It stores the state name and state code returned from the API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class State {
    private String name;
    private String stateCode;

    /**
     * Returns the state name.
     *
     * @return the state name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the state code.
     *
     * @return the state code
     */
    public String getStateCode() {
        return stateCode;
    }
}