package com.musicApp.backend.map.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventImageDTO {
    private String url;
    private Integer width;
    private Integer height;
    private String ratio;
    private Boolean fallback;
}