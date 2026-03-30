package com.musicApp.backend.controller;

import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;
import com.musicApp.backend.features.databasemodel.Review;
import com.musicApp.backend.features.databasemodel.ReviewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final AuthenticationUserRepository userRepository;

    public ReviewController(ReviewRepository reviewRepository, AuthenticationUserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest request) {
        if (request.getUserID() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing userID."));
        }
        if (request.getTargetName() == null || request.getTargetName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing review target name."));
        }
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rating must be between 1 and 5."));
        }

        AuthenticationUser user = userRepository.findById(request.getUserID())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Review review = new Review();
        review.setUser(user);
        review.setSong(null);
        review.setTargetType(request.getTargetType() == null ? "track" : request.getTargetType());
        review.setTargetName(request.getTargetName());
        review.setArtist(request.getArtist());
        review.setAlbum(request.getAlbum());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setDatePosted(LocalDate.now());

        Review saved = reviewRepository.save(review);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUserReviews(@PathVariable Long userId) {
        List<Review> reviews = reviewRepository.findByUser_Id(userId);
        List<Map<String, Object>> response = reviews.stream()
                .map(this::toReviewMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchReviews(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "minRating", defaultValue = "0") Integer minRating
    ) {
        if (query == null || query.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        List<Review> reviews = reviewRepository.searchByQuery(query.trim());
        List<Map<String, Object>> response = reviews.stream()
                .filter(r -> minRating == null || minRating <= 0 || (r.getRating() != null && r.getRating() >= minRating))
                .map(this::toReviewMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> toReviewMap(Review review) {
        Map<String, Object> result = new HashMap<>();
        result.put("reviewID", review.getReviewID());
        result.put("userId", review.getUser() != null ? review.getUser().getId() : null);
        result.put("targetType", review.getTargetType());
        result.put("targetName", review.getTargetName());
        result.put("artist", review.getArtist());
        result.put("album", review.getAlbum());
        result.put("rating", review.getRating());
        result.put("datePosted", review.getDatePosted());
        result.put("comment", review.getComment());
        return result;
    }

    public static class ReviewRequest {
        private Long userID;
        private String targetType;
        private String targetName;
        private String artist;
        private String album;
        private Integer rating;
        private String comment;

        public Long getUserID() {
            return userID;
        }

        public void setUserID(Long userID) {
            this.userID = userID;
        }

        public String getTargetType() {
            return targetType;
        }

        public void setTargetType(String targetType) {
            this.targetType = targetType;
        }

        public String getTargetName() {
            return targetName;
        }

        public void setTargetName(String targetName) {
            this.targetName = targetName;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
