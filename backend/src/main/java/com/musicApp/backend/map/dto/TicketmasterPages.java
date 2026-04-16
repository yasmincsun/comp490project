/**
 * Class Name: TicketmasterPages
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 */
package com.musicApp.backend.map.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents page information returned from the Ticketmaster API.
 * It is used as a data transfer object to store page data and provide access
 * to the total page information.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketmasterPages {

    private PagesDTO pages;

    /**
     * Returns the page information stored in this object.
     *
     * @return the {@link PagesDTO} object containing page information
     */
    public PagesDTO getTotalPages() {
        return pages;
    }
}