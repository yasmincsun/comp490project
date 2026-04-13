package com.musicApp.backend.features.friendship.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.musicApp.backend.features.friendship.model.Friendship;
import com.musicApp.backend.features.friendship.service.FriendshipService;
import com.musicApp.backend.features.notification.service.NotificationService;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;
import com.musicApp.backend.features.feed.model.Post;
import com.musicApp.backend.features.feed.repository.PostRepository;
import com.musicApp.backend.features.databasemodel.Review;
import com.musicApp.backend.features.databasemodel.ReviewRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/friendship")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuthenticationUserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addFriend(@RequestBody Map<String, Object> request) {
        try {
            Long user1Id = toLong(request.get("user1_id"));
            Long user2Id = toLong(request.get("user2_id"));

            if (user1Id == null || user2Id == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing user1_id or user2_id"));
            }

            Friendship friendship = friendshipService.addFriend(user1Id, user2Id);
            AuthenticationUser user1 = userRepository.findById(user1Id).orElse(null);
            AuthenticationUser user2 = userRepository.findById(user2Id).orElse(null);

            if (user1 != null && user2 != null) {
                if (Friendship.STATUS_PENDING.equals(friendship.getStatus())) {
                    String message = user1.getUsername() + " sent you a friend request.";
                    notificationService.createNotification(user2, user1, "friend_request", message);
                } else if (Friendship.STATUS_ACCEPTED.equals(friendship.getStatus())) {
                    String message = user2.getUsername() + " accepted your friend request.";
                    notificationService.createNotification(user2, user1, "friend_accepted", message);
                }
            }

            return ResponseEntity.ok(Map.of("friendship", friendship));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Error adding friend", "detail", e.getMessage()));
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestBody Map<String, Object> request) {
        try {
            Long requesterId = toLong(request.get("user1_id"));
            Long recipientId = toLong(request.get("user2_id"));
            Long notificationId = toLong(request.get("notification_id"));

            if (requesterId == null || recipientId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing user1_id or user2_id"));
            }

            Friendship friendship = friendshipService.acceptFriendRequest(requesterId, recipientId);
            AuthenticationUser requester = userRepository.findById(requesterId).orElse(null);
            AuthenticationUser recipient = userRepository.findById(recipientId).orElse(null);

            if (requester != null && recipient != null) {
                String message = recipient.getUsername() + " accepted your friend request.";
                notificationService.createNotification(requester, recipient, "friend_accepted", message);
            }
            if (notificationId != null && notificationId > 0) {
                notificationService.deleteNotification(notificationId);
            }
            return ResponseEntity.ok(Map.of("friendship", friendship));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Error accepting friend request", "detail", e.getMessage()));
        }
    }

    @PostMapping("/decline")
    public ResponseEntity<?> declineFriendRequest(@RequestBody Map<String, Object> request) {
        try {
            Long requesterId = toLong(request.get("user1_id"));
            Long recipientId = toLong(request.get("user2_id"));
            Long notificationId = toLong(request.get("notification_id"));

            if (requesterId == null || recipientId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing user1_id or user2_id"));
            }

            friendshipService.declineFriendRequest(requesterId, recipientId);
            if (notificationId != null && notificationId > 0) {
                notificationService.deleteNotification(notificationId);
            }
            return ResponseEntity.ok(Map.of("message", "Friend request declined"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Error declining friend request", "detail", e.getMessage()));
        }
    }

    @GetMapping("/status/{user1Id}/{user2Id}")
    public ResponseEntity<?> getFriendshipStatus(@PathVariable Long user1Id, @PathVariable Long user2Id) {
        try {
            String status = friendshipService.getFriendshipStatus(user1Id, user2Id);
            return ResponseEntity.ok(Map.of("status", status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error checking friendship status", "detail", e.getMessage()));
        }
    }

    @GetMapping("/activity/{userId}")
    public ResponseEntity<?> getFriendActivity(@PathVariable Long userId) {
        try {
            List<Long> friendIds = friendshipService.getAcceptedFriendIds(userId);
            List<Map<String, Object>> activity = new ArrayList<>();

            if (!friendIds.isEmpty()) {
                List<Post> posts = postRepository.findByAuthorIdInOrderByCreationDateDesc(friendIds);
                posts.forEach(post -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("type", "post");
                    item.put("id", post.getId());
                    item.put("author", post.getAuthor().getUsername());
                    item.put("authorId", post.getAuthor().getId());
                    item.put("content", post.getContent());
                    item.put("picture", post.getPicture());
                    item.put("createdAt", post.getCreationDate() != null ? post.getCreationDate().toString() : null);
                    activity.add(item);
                });

                List<Review> reviews = reviewRepository.findByUserIdsOrderByDatePostedDesc(friendIds);
                reviews.forEach(review -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("type", "review");
                    item.put("id", review.getReviewID());
                    item.put("author", review.getUser() != null ? review.getUser().getUsername() : null);
                    item.put("authorId", review.getUser() != null ? review.getUser().getId() : null);
                    item.put("comment", review.getComment());
                    item.put("rating", review.getRating());
                    item.put("targetName", review.getTargetName());
                    item.put("datePosted", review.getDatePosted() != null ? review.getDatePosted().toString() : null);
                    activity.add(item);
                });
            }

            activity.sort(Comparator.comparing(
                    item -> (String) (item.containsKey("createdAt") ? item.get("createdAt") : item.get("datePosted")),
                    Comparator.nullsLast(Comparator.reverseOrder())));

            return ResponseEntity.ok(activity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Error fetching friend activity", "detail", e.getMessage()));
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeFriend(@RequestBody Map<String, Object> request) {
        try {
            Long user1Id = toLong(request.get("user1_id"));
            Long user2Id = toLong(request.get("user2_id"));

            if (user1Id == null || user2Id == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing user1_id or user2_id"));
            }

            friendshipService.removeFriend(user1Id, user2Id);
            return ResponseEntity.ok(Map.of("message", "Friendship removed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Error removing friend", "detail", e.getMessage()));
        }
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return Long.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @GetMapping("/check/{user1Id}/{user2Id}")
    public ResponseEntity<?> areFriends(@PathVariable Long user1Id, @PathVariable Long user2Id) {
        try {
            boolean areFriends = friendshipService.areFriends(user1Id, user2Id);
            return ResponseEntity.ok(Map.of("areFriends", areFriends));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error checking friendship: " + e.getMessage());
        }
    }

    @GetMapping("/list/{userId}")
    public ResponseEntity<?> getFriendships(@PathVariable Long userId) {
        try {
            List<Friendship> friendships = friendshipService.getFriendships(userId);
            return ResponseEntity.ok(friendships);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching friendships: " + e.getMessage());
        }
    }
}
