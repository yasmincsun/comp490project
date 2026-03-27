package com.musicApp.backend.map.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.musicApp.backend.map.TicketMasterAPI.TicketMasterAPI;
import com.musicApp.backend.map.dto.EventDTO;

import reactor.core.publisher.Flux;


@Service
public class MapService{
  private final TicketMasterAPI ticketmasterAPI;

  public MapService(TicketMasterAPI ticketMasterAPI){
    this.ticketmasterAPI = ticketMasterAPI;
  }

  public Flux<EventDTO> getEventDTOs(String keyword){
    return ticketmasterAPI.getAllEvents(keyword);
  }


  //Create a method to check if tickets are on sale or not




}

