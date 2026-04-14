/**
 * Class Name: Status
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 * Description:
 * This enum represents the possible status values for an event.
 * It is used to match and convert event status values received from the API.
 */
package com.musicApp.backend.map.TicketMaster.Dates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    ONSALE("onsale"),
    OFFSALE("offsale"),
    CANCELED("canceled"),
    POSTPONED("postponed"),
    RESCHEDULED("rescheduled");

    private final String code;

    Status(String code) {
        this.code = code;
    }

    /**
     * Returns the string value of the event status.
     *
     * @return the status value as a string
     */
    @JsonValue
    public String getStatus() {
        return this.code;
    }

    /**
     * Converts a string value into its matching Status enum.
     *
     * @param value the string value of the event status
     * @return the matching {@link Status} enum value
     */
    @JsonCreator
    public static Status fromValue(String value) {
        for (Status status : Status.values()) {
            if (status.code.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}