package com.musicApp.backend.map.EmbeddedData;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.musicApp.backend.map.TicketMaster.util.Venue;

import lombok.Data;
import lombok.NoArgsConstructor;

// Puts events into lists


@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Embedded {
  private Venues[] venues;
}
