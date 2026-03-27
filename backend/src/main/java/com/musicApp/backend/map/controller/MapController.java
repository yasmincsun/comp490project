package com.musicApp.backend.map.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.musicApp.backend.map.dto.EventDTO;
import com.musicApp.backend.map.service.MapService;

import reactor.core.publisher.Flux;


@CrossOrigin(origins = "http://127.0.0.1:5173")
@RestController
@RequestMapping("/api/v1/map")
public class MapController {

  private final MapService mapService;

  public MapController(MapService mapService){
    this.mapService = mapService;
  }

@GetMapping("/search")
public Flux<EventDTO> listOfEvents(@RequestParam String keyword){
  return mapService.getEventDTOs(keyword);
}

}
