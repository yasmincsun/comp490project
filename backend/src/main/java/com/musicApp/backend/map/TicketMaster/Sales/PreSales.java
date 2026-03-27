package com.musicApp.backend.map.TicketMaster.Sales;

import java.time.Instant;

public class PreSales {

  private Instant startDateTime;
  private Instant endDateTime;
  private String name;
  
  public Instant getStartDateTime(){
    return startDateTime;
  }

  public Instant getEndDateTime(){
    return endDateTime;
  }

  public String getName(){
    return name;
  }

}
