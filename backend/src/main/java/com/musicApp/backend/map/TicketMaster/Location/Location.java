/**
 * Class Name: Location
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 * Description:
 * This class represents the geographic location of a venue or event.
 * It stores the longitude and latitude coordinates returned from the API.
 */
package com.musicApp.backend.map.TicketMaster.Location;

public class Location {

    private Double longitude;
    private Double latitude;

    /**
     * Returns the longitude coordinate.
     *
     * @return the longitude value
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Returns the latitude coordinate.
     *
     * @return the latitude value
     */
    public Double getLatitude() {
        return latitude;
    }
}