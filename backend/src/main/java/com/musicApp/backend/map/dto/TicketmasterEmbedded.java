/**
 * Class Name: TicketmasterEmbedded
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 */
package com.musicApp.backend.map.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 * This class represents the embedded event data returned from the Ticketmaster
 * API. It is used as a data transfer object to store a list of event objects.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketmasterEmbedded {
    private List<EventDTO> events;
}