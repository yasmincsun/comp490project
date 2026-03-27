package com.musicApp.backend.map.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.musicApp.backend.map.EmbeddedData.Embedded;
import com.musicApp.backend.map.TicketMaster.Accessibility.EventAccessibility;
import com.musicApp.backend.map.TicketMaster.Age.Age;
import com.musicApp.backend.map.TicketMaster.Classifications.Classifications;
import com.musicApp.backend.map.TicketMaster.Dates.Dates;
import com.musicApp.backend.map.TicketMaster.Images.TicketMasterImages;
import com.musicApp.backend.map.TicketMaster.Location.Location;
import com.musicApp.backend.map.TicketMaster.Promoter.Promoter;
import com.musicApp.backend.map.TicketMaster.Sales.Sales;
import com.musicApp.backend.map.TicketMaster.SeatMap.SeatMap;
import com.musicApp.backend.map.TicketMaster.TicketLimit.TicketLimit;
import com.musicApp.backend.map.TicketMaster.Ticketing.Ticketing;
import com.musicApp.backend.map.TicketMaster.util.Venue;
import com.musicApp.backend.map.entity.Events;

import lombok.Data;
import lombok.NoArgsConstructor;


/// Looks at a single DTO

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDTO {
    private String name;
    private String id;
    private String url;               //fix this
    // private List<TicketMasterImages> images;
    private Dates dates;
    private String info;
    private Age ageRestrictions;
    private Ticketing ticketing;
    private String nameOrigin;

    @JsonProperty("_embedded")
    private Embedded embedded;
}