package com.musicApp.backend.map.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.musicApp.backend.map.TicketMaster.Accessibility.EventAccessibility;
import com.musicApp.backend.map.TicketMaster.Age.Age;
import com.musicApp.backend.map.TicketMaster.Classifications.Classifications;
import com.musicApp.backend.map.TicketMaster.Dates.Dates;
import com.musicApp.backend.map.TicketMaster.Images.TicketMasterImages;
import com.musicApp.backend.map.TicketMaster.Promoter.Promoter;
import com.musicApp.backend.map.TicketMaster.Sales.Sales;
import com.musicApp.backend.map.TicketMaster.SeatMap.SeatMap;
import com.musicApp.backend.map.TicketMaster.TicketLimit.TicketLimit;
import com.musicApp.backend.map.TicketMaster.Ticketing.Ticketing;
import com.musicApp.backend.map.TicketMaster.util.*;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Events {

  private String name;
  private String id;
  private String type;
  private Boolean test;
  private String url;
  private String locale;
  private List<TicketMasterImages> images; 
  private Sales sales;
  private Dates dates;
  private List<Classifications> classifications;
  private Promoter promoter;
  private List<Promoter> promoters;
  private String info;
  private SeatMap seatmap;

  @JsonProperty("accessibility")
  private EventAccessibility eventAccessibility;
  private TicketLimit ticketLimit;

  @JsonProperty("ageRestrictions")
  private Age ageRestrictions;
  private Ticketing ticketing;
  private String nameOrigin;

  @JsonProperty("_embedded")
  private Embedded embedded;

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setTest(Boolean test) {
    this.test = test;
  }

  public Boolean getTest() {
    return test;
  }

  public void setUrl(String url) {
    this.url = url;
  }
  public String getUrl() {
    return url;
  }
  
  public void setLocale(String locale) {
    this.locale = locale;
  }

  public String getLocale() {
    return locale;
  }

  public void setImages(List<TicketMasterImages> images) {
    this.images = images;
  }

  public List<TicketMasterImages> getImages() {
    return images;
  }

  public void setSales(Sales sales) {
    this.sales = sales;
  }

  public Sales getSales() {
    return sales;
  }

  public void setDates(Dates dates) {
    this.dates = dates;
  }

  public Dates getDates() {
    return dates;
  }

  public void setClassifications(List<Classifications> classifications) {
    this.classifications = classifications;
  }

  public List<Classifications> getClassifications() {
    return classifications;
  }

  public void setPromoter(Promoter promoter) {
    this.promoter = promoter;
  }

  public Promoter getPromoter() {
    return promoter;
  }

  public void setPromoters(List<Promoter> promoters) {
    this.promoters = promoters;
  }

  public List<Promoter> getPromoters() {
    return promoters;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public String getInfo() {
    return info;
  }

  public void setSeatmap(SeatMap seatmap) {
    this.seatmap = seatmap;
  }

  public SeatMap getSeatmap() {
    return seatmap;
  }

  public void setEventAccessibility(EventAccessibility eventAccessibility) {
    this.eventAccessibility = eventAccessibility;
  }

  public EventAccessibility getEventAccessibility() {
    return eventAccessibility;
  }

  public void setTicketLimit(TicketLimit ticketLimit) {
    this.ticketLimit = ticketLimit;
  }

  public TicketLimit getTicketLimit() {
    return ticketLimit;
  }

  public void setAgeRestrictions(Age ageRestrictions) {
    this.ageRestrictions = ageRestrictions;
  }

  public Age getAgeRestrictions() {
    return ageRestrictions;
  }

  public void setTicketing(Ticketing ticketing) {
    this.ticketing = ticketing;
  }

  public Ticketing getTicketing() {
    return ticketing;
  }

  public void setNameOrigin(String nameOrigin) {
    this.nameOrigin = nameOrigin;
  }

  public String getNameOrigin() {
    return nameOrigin;
  }

  public void setEmbedded(Embedded embedded) {
    this.embedded = embedded;
  }

  public Embedded getEmbedded() {
    return embedded;
  }

}
