/**
 * Class Name: EventDTO
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 * Description:
 * This class represents a single event data transfer object used in the
 * application. It stores event information received from the API, including
 * the event name, id, URL, date details, age restrictions, ticketing
 * information, and embedded related data.
 */
package com.musicApp.backend.map.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.musicApp.backend.map.EmbeddedData.Embedded;
import com.musicApp.backend.map.TicketMaster.Age.Age;
import com.musicApp.backend.map.TicketMaster.Dates.Dates;
import com.musicApp.backend.map.TicketMaster.Ticketing.Ticketing;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDTO {
    private String name;
    private String id;
    private String url;
    private Dates dates;
    private String info;
    private Age ageRestrictions;
    private Ticketing ticketing;
    private String nameOrigin;

    @JsonProperty("_embedded")
    private Embedded embedded;
}