package com.musicApp.backend.map.TicketMaster.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.musicApp.backend.map.TicketMaster.Venues.Address;
import com.musicApp.backend.map.TicketMaster.Venues.City;
import com.musicApp.backend.map.TicketMaster.Venues.State;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Venue {
  private String name;
  private Location location;

  private City city;
  private State state;
  private Address address;

  public String getName() { 
    return name; 
  }
  public void setName(String name) {
     this.name = name; 
  }

  public Location getLocation() { 
    return location; 
  }
  public void setLocation(Location location) {
     this.location = location; 
  }

  public City getCity() {
     return city;
  }
  public void setCity(City city) {
     this.city = city; 
  }

  public State getState() { return state; }
  public void setState(State state) { this.state = state; }

  public Address getAddress() { return address; }
  public void setAddress(Address address) { this.address = address; }

}
