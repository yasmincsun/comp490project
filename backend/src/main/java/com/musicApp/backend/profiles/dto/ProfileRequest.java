/**
 * Class Name: ProfileRequest
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 */
package com.musicApp.backend.profiles.dto;

/**
 * This record represents the profile data returned by the application.
 * It stores user profile information such as name, username, email,
 * bio, color, profile image details, and favorite music preferences.
 */
public record ProfileRequest(
    long id,
    String username,
    String name,
    String lastName,
    String email,
    String bio,
    Integer color,
    String imageKey,
    Long profileImageUpdatedAt,
    String profileImageUrl,
    long profileImageUrlExpiresInSeconds,
    String favoriteArtists,
    String favoriteSongs
) {}


