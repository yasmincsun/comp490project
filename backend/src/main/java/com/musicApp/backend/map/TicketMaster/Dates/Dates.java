package com.musicApp.backend.map.TicketMaster.Dates;

import com.musicApp.backend.map.dto.EventStatusDTO;

public class Dates {
  private Start start;
  private String timezone;
  private EventStatusDTO status;
  private Boolean spanMultipleDays;

  public Start getStart(){
    return start;
  }

  public String timeZone(){
    return timezone;
  }

  public EventStatusDTO getStatus(){ 
    return status;
  }

  public Boolean getSpanMultipleDays(){
    return spanMultipleDays;
  }
  
}
