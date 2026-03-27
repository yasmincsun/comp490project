package com.musicApp.backend.map.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

/// Looks at a single DTO

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagesDTO {
  private int totalPages;
}