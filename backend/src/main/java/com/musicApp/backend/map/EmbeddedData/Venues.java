/**
 * Class Name: Venues
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 */

package com.musicApp.backend.map.EmbeddedData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.musicApp.backend.map.TicketMaster.Address.Address;
import com.musicApp.backend.map.TicketMaster.City.City;
import com.musicApp.backend.map.TicketMaster.Country.Country;
import com.musicApp.backend.map.TicketMaster.Location.Location;
import com.musicApp.backend.map.TicketMaster.State.State;

/**
 * This class represents venue information associated with an event.
 * It stores the venue name, location, address, city, state, country,
 * and postal code data returned from the API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Venues {
    private String name;
    private Location location;
    private Address address;
    private City city;
    private State state;
    private Country country;
    private String postalCode;

    /**
     * Returns the venue name.
     *
     * @return the venue name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the location information for the venue.
     *
     * @return the {@link Location} object containing venue location data
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Returns the address information for the venue.
     *
     * @return the {@link Address} object containing venue address data
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Returns the city information for the venue.
     *
     * @return the {@link City} object containing venue city data
     */
    public City getCity() {
        return city;
    }

    /**
     * Returns the state information for the venue.
     *
     * @return the {@link State} object containing venue state data
     */
    public State getState() {
        return state;
    }

    /**
     * Returns the country information for the venue.
     *
     * @return the {@link Country} object containing venue country data
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Returns the postal code for the venue.
     *
     * @return the venue postal code
     */
    public String getPostalCode() {
        return postalCode;
    }
}