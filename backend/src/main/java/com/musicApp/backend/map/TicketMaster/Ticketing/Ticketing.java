/**
 * Class Name: Ticketing
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 */
package com.musicApp.backend.map.TicketMaster.Ticketing;

/**
 * This class represents ticketing information for an event.
 * It stores SafeTix details and all-inclusive pricing information
 * returned from the API.
 */
public class Ticketing {
    private SafeTix safeTix;
    private AllInclusivePricing allInclusivePricing;

    /**
     * Returns the SafeTix information for the event.
     *
     * @return the {@link SafeTix} object containing SafeTix details
     */
    public SafeTix getSafeTix() {
        return safeTix;
    }

    /**
     * Returns the all-inclusive pricing information for the event.
     *
     * @return the {@link AllInclusivePricing} object containing pricing details
     */
    public AllInclusivePricing getAllInclusivePricing() {
        return allInclusivePricing;
    }
}