package com.musicApp.backend.map.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

// matches the top-level responses


@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketmasterResponse {

    @JsonProperty("_embedded")
    private TicketmasterEmbedded embedded;

    @JsonProperty("page")
    private PagesDTO pages;

}