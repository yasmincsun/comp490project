/**
 * Class Name: Dates
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 */
package com.musicApp.backend.map.TicketMaster.Dates;

import com.musicApp.backend.map.dto.EventStatusDTO;

/**
 * This class represents date and time information for an event.
 * It stores the event start details, timezone, status, and whether
 * the event spans multiple days.
 */
public class Dates {
    private Start start;
    private String timezone;
    private EventStatusDTO status;
    private Boolean spanMultipleDays;

    /**
     * Returns the start date and time information for the event.
     *
     * @return the {@link Start} object containing event start details
     */
    public Start getStart() {
        return start;
    }

    /**
     * Returns the timezone for the event.
     *
     * @return the timezone of the event
     */
    public String timeZone() {
        return timezone;
    }

    /**
     * Returns the status information for the event.
     *
     * @return the {@link EventStatusDTO} object containing event status data
     */
    public EventStatusDTO getStatus() {
        return status;
    }

    /**
     * Returns whether the event spans multiple days.
     *
     * @return {@code true} if the event spans multiple days, {@code false} otherwise
     */
    public Boolean getSpanMultipleDays() {
        return spanMultipleDays;
    }
}
