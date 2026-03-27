package com.musicApp.backend.map.EmbeddedData;

import com.musicApp.backend.map.TicketMaster.Address.Address;
import com.musicApp.backend.map.TicketMaster.Location.Location;

public class Venues {
  private Location location;
  private Address address;

  public Location getLocation(){
    return location;
  }

  public Address getAddress(){
    return address;
  }
}
