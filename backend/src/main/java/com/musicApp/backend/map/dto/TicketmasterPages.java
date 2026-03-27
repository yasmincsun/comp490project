package com.musicApp.backend.map.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

// Puts events into lists


@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketmasterPages {

    private PagesDTO pages;

    public PagesDTO getTotalPages(){
      return pages;
    }
}