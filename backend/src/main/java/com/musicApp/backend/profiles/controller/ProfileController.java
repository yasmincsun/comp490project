package com.musicApp.backend.profiles.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.service.AuthenticationService;
import com.musicApp.backend.features.authentication.utils.EmailService;
import com.musicApp.backend.profiles.dto.ProfileRequest;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;

import org.springframework.beans.factory.annotation.Value;
import java.time.Duration;
import java.util.Set;



@CrossOrigin(origins = "http://127.0.0.1:5173") // adjust if frontend uses a different port
@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {
  private final AuthenticationService authenticationService;
  private final EmailService emailService;
  private final AuthenticationUserRepository authenticationUserRepository;
  private final S3Presigner presigner;
  private final String bucket;
  private final String publicBaseUrl;


@Autowired
public ProfileController(AuthenticationUserRepository authenticationUserRepository, 
                          AuthenticationService authenticationService,
                            EmailService emailService,
                              S3Presigner presigner,
                                 @Value("${r2.bucket}") String bucket,
                                    @Value("${r2.url}") String publicBaseUrl){
                              this.authenticationService = authenticationService;
                              this.authenticationUserRepository = authenticationUserRepository;
                              this.emailService = emailService;
                              this.presigner = presigner;
                              this.bucket = bucket;
                              this.publicBaseUrl = publicBaseUrl;
                            }


@GetMapping
public ProfileRequest getProfile(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser) {
  AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
  return toProfileRequest(user);
}

   @PutMapping("/fname")
   public void updateName(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser, @RequestParam String name){
     AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
     user.setName(name);
     authenticationUserRepository.save(user);
   }

  @PutMapping("/lname")
   public void updateLastName(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser, @RequestParam String lname){
     AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
    user.setLastName(lname);;
    authenticationUserRepository.save(user);
   }

   @PutMapping("/userName")
   public void updateUserName(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser, @RequestParam String userName){
     AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
     user.setUsername(userName);
     authenticationUserRepository.save(user);
   }
  
  @PutMapping("/bio")
  public void updateBio(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser, @RequestParam String bio){
     AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
    user.setBio(bio);
    authenticationUserRepository.save(user);
  }

  @PutMapping("/color")
  public void updateColor(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser, @RequestParam int color){
     AuthenticationUser user = authenticationService.getUser(authenticationUser.getEmail());
    user.setColor(color);
    authenticationUserRepository.save(user);
  } 

  // request body DTO (simple)
  public record UploadUrlRequest(String contentType, Long fileSize) {}
  public record UploadUrlResponse(String uploadUrl, String objectKey, long expiresInSeconds) {}

  @PostMapping("/picture/upload-url")
  public UploadUrlResponse getProfilePicUploadUrl(
      @RequestAttribute("authenticatedUser") AuthenticationUser authUser,
      @RequestBody UploadUrlRequest body
  ) {
    // 1) Validate inputs
    String contentType = body.contentType();
    if (contentType == null) throw new IllegalArgumentException("contentType is required");

    Set<String> allowed = Set.of("image/jpeg", "image/png", "image/webp");
    if (!allowed.contains(contentType)) {
      throw new IllegalArgumentException("Unsupported contentType: " + contentType);
    }

    long maxBytes = 3 * 1024 * 1024; // 3MB example limit
    if (body.fileSize() != null && body.fileSize() > maxBytes) {
      throw new IllegalArgumentException("File too large");
    }

    // 2) Choose object key: avatars/<userId>/profile.jpg
    long userId = authUser.getId(); // or look up user by email if needed
    String objectKey = "avatars/" + userId + "/profile.jpg";

    // 3) Create PutObjectRequest to be presigned
    PutObjectRequest putReq = PutObjectRequest.builder()
        .bucket(bucket)               // "moody-app"
        .key(objectKey)
        .contentType(contentType)    
        .build();

    // 4) Presign it (short-lived)
    Duration ttl = Duration.ofMinutes(5);
    PutObjectPresignRequest presignReq = PutObjectPresignRequest.builder()
        .signatureDuration(ttl)
        .putObjectRequest(putReq)
        .build();

    String uploadUrl = presigner.presignPutObject(presignReq).url().toString();

    return new UploadUrlResponse(uploadUrl, objectKey, ttl.toSeconds());
  }

@PutMapping("/picture")
public ProfileRequest saveProfilePictureKey(
    @RequestAttribute("authenticatedUser") AuthenticationUser authUser,
    @RequestBody SavePictureRequest body
) {
  AuthenticationUser user = authenticationService.getUser(authUser.getEmail());

  String expectedKey = "avatars/" + user.getId() + "/profile.jpg";
  if (body == null || body.objectKey() == null) throw new IllegalArgumentException("objectKey is required");
  if (!expectedKey.equals(body.objectKey())) throw new IllegalArgumentException("Invalid objectKey for this user");

  user.setImageKey(expectedKey);
  user.setProfileImageUpdatedAt(System.currentTimeMillis());
  authenticationUserRepository.save(user);

  return toProfileRequest(user);
}

public record SavePictureRequest(String objectKey) {}
public record SavePictureResponse(String objectKey, String publicUrl, long updatedAt) {}

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
      user.getBio(),
      user.getColor(),
      imageKey,
      updatedAt,
      presignedGetUrl,
      expires
  );
}

}
