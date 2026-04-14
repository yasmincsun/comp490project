/**
 * Class Name: ProfileController
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 * Description:
 * This class handles profile-related requests in the application.
 * It allows users to view and update profile information, manage
 * profile pictures, update account details, and search for other users.
 */
package com.musicApp.backend.profiles.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.service.AuthenticationService;
import com.musicApp.backend.features.authentication.utils.EmailService;
import com.musicApp.backend.features.authentication.utils.Encoder;
import com.musicApp.backend.profiles.dto.ProfileRequest;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import org.springframework.beans.factory.annotation.Value;
import java.time.Duration;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://127.0.0.1:5173")
@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {
    private final AuthenticationService authenticationService;
    private final EmailService emailService;
    private final AuthenticationUserRepository authenticationUserRepository;
    private final S3Presigner presigner;
    private final Encoder encoder;
    private final String bucket;
    private final String publicBaseUrl;

    /**
     * Creates a ProfileController object with the required services and configuration values.
     *
     * @param authenticationUserRepository the repository used to store and retrieve user data
     * @param authenticationService the service used to access authenticated user information
     * @param emailService the service used for email-related features
     * @param presigner the S3 presigner used to generate upload and download URLs
     * @param encoder the encoder used to encrypt passwords
     * @param bucket the storage bucket used for profile pictures
     * @param publicBaseUrl the public base URL for stored profile pictures
     */
    @Autowired
    public ProfileController(
            AuthenticationUserRepository authenticationUserRepository,
            AuthenticationService authenticationService,
            EmailService emailService,
            S3Presigner presigner,
            Encoder encoder,
            @Value("${r2.bucket}") String bucket,
            @Value("${r2.url}") String publicBaseUrl
    ) {
        this.authenticationService = authenticationService;
        this.authenticationUserRepository = authenticationUserRepository;
        this.emailService = emailService;
        this.presigner = presigner;
        this.encoder = encoder;
        this.bucket = bucket;
        this.publicBaseUrl = publicBaseUrl;
    }

    /**
     * Returns the profile information for the authenticated user.
     *
     * @param authenticationUser the authenticated user taken from the request
     * @return a {@link ProfileRequest} containing the user's profile information
     */
    @GetMapping
    public ProfileRequest getProfile(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser) {
        AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
        return toProfileRequest(user);
    }

    /**
     * Updates the first name of the authenticated user.
     *
     * @param authenticationUser the authenticated user taken from the request
     * @param name the new first name to save
     */
    @PutMapping("/fname")
    public void updateName(
            @RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser,
            @RequestParam String name
    ) {
        AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
        user.setName(name);
        authenticationUserRepository.save(user);
    }

    /**
     * Updates the last name of the authenticated user.
     *
     * @param authenticationUser the authenticated user taken from the request
     * @param lname the new last name to save
     */
    @PutMapping("/lname")
    public void updateLastName(
            @RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser,
            @RequestParam String lname
    ) {
        AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
        user.setLastName(lname);
        authenticationUserRepository.save(user);
    }

    /**
     * Updates the username of the authenticated user.
     *
     * @param authenticationUser the authenticated user taken from the request
     * @param userName the new username to save
     */
    @PutMapping("/userName")
    public void updateUserName(
            @RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser,
            @RequestParam String userName
    ) {
        AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
        user.setUsername(userName);
        authenticationUserRepository.save(user);
    }

    /**
     * Updates the biography of the authenticated user.
     *
     * @param authenticationUser the authenticated user taken from the request
     * @param bio the new biography text to save
     */
    @PutMapping("/bio")
    public void updateBio(
            @RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser,
            @RequestParam String bio
    ) {
        AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
        user.setBio(bio);
        authenticationUserRepository.save(user);
    }

    /**
     * Updates the profile color of the authenticated user.
     *
     * @param authenticationUser the authenticated user taken from the request
     * @param color the new color value to save
     */
    @PutMapping("/color")
    public void updateColor(
            @RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser,
            @RequestParam int color
    ) {
        AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
        user.setColor(color);
        authenticationUserRepository.save(user);
    }

    /**
     * Updates the favorite artists of the authenticated user.
     *
     * @param authenticationUser the authenticated user taken from the request
     * @param favoriteArtists the new favorite artists value to save
     */
    @PutMapping("/favorites/artists")
    public void updateFavoriteArtists(
            @RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser,
            @RequestParam String favoriteArtists
    ) {
        AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
        user.setFavoriteArtists(favoriteArtists);
        authenticationUserRepository.save(user);
    }

    /**
     * Updates the favorite songs of the authenticated user.
     *
     * @param authenticationUser the authenticated user taken from the request
     * @param favoriteSongs the new favorite songs value to save
     */
    @PutMapping("/favorites/songs")
    public void updateFavoriteSongs(
            @RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser,
            @RequestParam String favoriteSongs
    ) {
        AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
        user.setFavoriteSongs(favoriteSongs);
        authenticationUserRepository.save(user);
    }

    public record AccountUpdateRequest(String firstName, String lastName, String password) {}

    /**
     * Updates account information for the authenticated user.
     *
     * @param authenticationUser the authenticated user taken from the request
     * @param request the request body containing updated first name, last name, or password
     */
    @PutMapping("/account")
    public void updateAccountInfo(
            @RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser,
            @RequestBody AccountUpdateRequest request
    ) {
        AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());

        if (request.firstName() != null && !request.firstName().isBlank()) {
            user.setName(request.firstName());
        }
        if (request.lastName() != null && !request.lastName().isBlank()) {
            user.setLastName(request.lastName());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(encoder.encode(request.password()));
        }

        authenticationUserRepository.save(user);
    }

    public record UploadUrlRequest(String contentType, Long fileSize) {}
    public record UploadUrlResponse(String uploadUrl, String objectKey, long expiresInSeconds) {}

    /**
     * Generates a presigned upload URL for a profile picture.
     *
     * @param authUser the authenticated user taken from the request
     * @param body the request body containing file type and file size information
     * @return an {@link UploadUrlResponse} containing the upload URL, object key, and expiration time
     */
    @PostMapping("/picture/upload-url")
    public UploadUrlResponse getProfilePicUploadUrl(
            @RequestAttribute("authenticatedUser") AuthenticationUser authUser,
            @RequestBody UploadUrlRequest body
    ) {
        String contentType = body.contentType();
        if (contentType == null) {
            throw new IllegalArgumentException("contentType is required");
        }

        Set<String> allowed = Set.of("image/jpeg", "image/png", "image/webp");
        if (!allowed.contains(contentType)) {
            throw new IllegalArgumentException("Unsupported contentType: " + contentType);
        }

        long maxBytes = 3 * 1024 * 1024;
        if (body.fileSize() != null && body.fileSize() > maxBytes) {
            throw new IllegalArgumentException("File too large");
        }

        long userId = authUser.getId();
        String objectKey = "avatars/" + userId + "/profile.jpg";

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .build();

        Duration ttl = Duration.ofMinutes(5);
        PutObjectPresignRequest presignReq = PutObjectPresignRequest.builder()
                .signatureDuration(ttl)
                .putObjectRequest(putReq)
                .build();

        String uploadUrl = presigner.presignPutObject(presignReq).url().toString();

        return new UploadUrlResponse(uploadUrl, objectKey, ttl.toSeconds());
    }

    /**
     * Saves the profile picture key for the authenticated user.
     *
     * @param authUser the authenticated user taken from the request
     * @param body the request body containing the object key for the uploaded image
     * @return a {@link ProfileRequest} containing the updated profile information
     */
    @PutMapping("/picture")
    public ProfileRequest saveProfilePictureKey(
            @RequestAttribute("authenticatedUser") AuthenticationUser authUser,
            @RequestBody SavePictureRequest body
    ) {
        AuthenticationUser user = authenticationService.getUser(authUser.getEmail());

        String expectedKey = "avatars/" + user.getId() + "/profile.jpg";
        if (body == null || body.objectKey() == null) {
            throw new IllegalArgumentException("objectKey is required");
        }
        if (!expectedKey.equals(body.objectKey())) {
            throw new IllegalArgumentException("Invalid objectKey for this user");
        }

        user.setImageKey(expectedKey);
        user.setProfileImageUpdatedAt(System.currentTimeMillis());
        authenticationUserRepository.save(user);

        return toProfileRequest(user);
    }

    public record SavePictureRequest(String objectKey) {}
    public record SavePictureResponse(String objectKey, String publicUrl, long updatedAt) {}

    /**
     * Converts an AuthenticationUser object into a ProfileRequest object.
     *
     * @param user the user whose data will be converted
     * @return a {@link ProfileRequest} containing formatted profile data
     */
    private ProfileRequest toProfileRequest(AuthenticationUser user) {
        String imageKey = user.getImageKey();
        Long updatedAt = user.getProfileImageUpdatedAt();

        String presignedGetUrl = null;
        long expires = 0;

        if (imageKey != null && !imageKey.isBlank()) {
            var getReq = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(imageKey)
                    .build();

            var ttl = Duration.ofMinutes(10);
            var presignReq = GetObjectPresignRequest.builder()
                    .signatureDuration(ttl)
                    .getObjectRequest(getReq)
                    .build();

            presignedGetUrl = presigner.presignGetObject(presignReq).url().toString();
            expires = ttl.toSeconds();
        }

        return new ProfileRequest(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getBio(),
                user.getColor(),
                imageKey,
                updatedAt,
                presignedGetUrl,
                expires,
                user.getFavoriteArtists(),
                user.getFavoriteSongs()
        );
    }

    /**
     * Searches for users whose usernames match the given query.
     *
     * @param query the text used to search for matching usernames
     * @return a list of {@link ProfileRequest} objects for matching users
     */
    @GetMapping("/search")
    public List<ProfileRequest> searchUsers(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        String lowerQuery = query.toLowerCase().trim();

        List<AuthenticationUser> allUsers = authenticationUserRepository.findAll();
        return allUsers.stream()
                .filter(user -> user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerQuery))
                .map(this::toProfileRequest)
                .collect(Collectors.toList());
    }
}