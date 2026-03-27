package com.musicApp.backend.map.TicketMaster.Sales;

import java.time.Instant;

public class PublicSale {
  private Instant startDateTime;
  private Boolean startTBD;
  private Boolean startTBA;
  private Instant endDateTime; 

  public Instant getStartDateTime(){
    return startDateTime;
  }

  public Instant getEndDateTime(){
    return endDateTime;
  }

  public Boolean getStartTBD(){
    return startTBD;
  }

  public Boolean getStartTBA(){
    return startTBA;
  }
 
}
