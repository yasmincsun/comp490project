/**
 * Class Name: EventStatusDTO
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 */
package com.musicApp.backend.map.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.musicApp.backend.map.TicketMaster.Dates.Status;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 * This class represents the status information for an event. It is used as a
 * data transfer object to store the event status code received from the API.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventStatusDTO {
    private Status code;
}