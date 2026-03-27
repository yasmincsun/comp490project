package com.musicApp.backend.map.TicketMaster.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
  public class Attraction {
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
  }
