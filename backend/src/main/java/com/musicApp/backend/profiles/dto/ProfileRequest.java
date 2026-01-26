package com.musicApp.backend.profiles.dto;

  public record ProfileRequest(
    long id,
    String username,
    String name,
    String lastName,
    String bio,
    Integer color,
    String imageKey,
    Long profileImageUpdatedAt,
    String profileImageUrl,          // presigned GET URL (nullable if no image)
    long profileImageUrlExpiresInSeconds
) {}


