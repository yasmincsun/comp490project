package com.musicApp.backend.map.TicketMaster.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Embedded {
    private List<Venue> venues;
    private List<Attraction> attractions;

    public List<Venue> getVenues() { return venues; }
    public void setVenues(List<Venue> venues) { this.venues = venues; }

    public List<Attraction> getAttractions() { return attractions; }
    public void setAttractions(List<Attraction> attractions) { this.attractions = attractions; }
  }