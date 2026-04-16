/**
 * Class Name: Age
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 */
package com.musicApp.backend.map.TicketMaster.Age;

/**
 * This class represents age restriction information for an event.
 * It stores whether legal age enforcement applies.
 */
public class Age {
    private Boolean legalAgeEnforced;

    /**
     * Returns whether legal age enforcement applies to the event.
     *
     * @return {@code true} if legal age is enforced, {@code false} otherwise
     */
    public Boolean getLegalAgeEnforced() {
        return legalAgeEnforced;
    }
}
