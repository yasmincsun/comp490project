/**
 * Class Name: TicketmasterResponse
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 * Description:
 * This class matches the top-level response returned from the Ticketmaster API.
 * It stores the embedded event data and page information from the response.
 */
package com.musicApp.backend.map.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketmasterResponse {

    @JsonProperty("_embedded")
    private TicketmasterEmbedded embedded;

    @JsonProperty("page")
    private PagesDTO pages;
}