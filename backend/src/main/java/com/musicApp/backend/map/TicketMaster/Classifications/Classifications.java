package com.musicApp.backend.map.TicketMaster.Classifications;

public class Classifications {
  private Boolean primary;
  private Segment segment;
  private Genre genre;
  private SubGenre subGenre;
  private EventType eventType;
  private SubType subType;
  private Boolean family;

  public Boolean getPrimary(){
    return primary;
  }

  public Segment getSegment(){
    return segment;
  }

  public Genre genre(){
    return genre;
  }

  public SubGenre getSubGenre(){
    return subGenre;
  }

  public EventType eventType(){
    return eventType;
  }

  public SubType subType(){
    return subType;
  }
 
  public Boolean family(){
    return family;
  } 
}
