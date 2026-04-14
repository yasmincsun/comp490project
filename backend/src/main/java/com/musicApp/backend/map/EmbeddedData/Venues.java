/**
 * Class Name: Venues
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 * Description:
 * This class represents venue information associated with an event.
 * It stores the venue location and address data returned from the API.
 */
package com.musicApp.backend.map.EmbeddedData;

import com.musicApp.backend.map.TicketMaster.Address.Address;
import com.musicApp.backend.map.TicketMaster.Location.Location;

public class Venues {
    private Location location;
    private Address address;

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
}