package com.musicApp.backend.map.TicketMaster.Dates;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
 
public class Start {
  private LocalDate localDate;
  private LocalTime localTime;
  private Instant dateTime;
  private Boolean privdateTBD;
  private Boolean dateTBA;
  private Boolean timeTBA;
  private Boolean noSpecificTime;

  public LocalDate getLocalDate(){
    return localDate;
  }

  public LocalTime getLocalTime(){
    return localTime;
  }

  public Instant getDateTime(){
    return dateTime;
  }

  public Boolean getPrivdateTBD(){
    return privdateTBD;
  }

  public Boolean getDateTBA(){
    return dateTBA;
  }

  public Boolean getTimeTBA(){
    return timeTBA;
  }

  public Boolean getNoSpecificTime(){
    return noSpecificTime;
  }
  
}
