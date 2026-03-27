package com.musicApp.backend.map.TicketMaster.Address;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {
  String line1;
  String line2;
  String line3;

  public String getLine1(){
    return line1;
  }

  public String getLine2(){
    return line2;
  }

  public String getLine3(){
    return line3;
  }
}
