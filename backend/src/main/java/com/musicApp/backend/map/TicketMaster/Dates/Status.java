package com.musicApp.backend.map.TicketMaster.Dates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
  ONSALE("onsale"),
  OFFSALE("offsale"),
  CANCELED("canceled"),
  POSTPONED("postponed"),
  RESCHEDULED("rescheduled");

  private final String code;

  Status(String code) {
    this.code = code;
  }

  @JsonValue
  public String getStatus(){
    return this.code;
  }

  @JsonCreator
  public static Status fromValue(String value) {
      for (Status status : Status.values()) {
          if (status.code.equalsIgnoreCase(value)) {
              return status;
          }
        }
      throw new IllegalArgumentException("Unknown status: " + value);
  }
}
