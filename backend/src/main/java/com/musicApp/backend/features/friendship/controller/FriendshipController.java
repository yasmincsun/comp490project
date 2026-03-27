package com.musicApp.backend.features.friendship.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.musicApp.backend.features.friendship.model.Friendship;
import com.musicApp.backend.features.friendship.service.FriendshipService;
import com.musicApp.backend.features.notification.service.NotificationService;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;
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

    /**
     * Add a user as a friend.
     * 
     * @param request should contain user1_id and user2_id
     * @return the created Friendship record
     */
    @PostMapping("/add")
    public ResponseEntity<?> addFriend(@RequestBody Map<String, Long> request) {
        try {
            Long user1Id = request.get("user1_id");
            Long user2Id = request.get("user2_id");

            if (user1Id == null || user2Id == null) {
                return ResponseEntity.badRequest().body("Missing user1_id or user2_id");
            }

            Friendship friendship = friendshipService.addFriend(user1Id, user2Id);
            
            // Create a notification for the second user
            try {
                AuthenticationUser user1 = userRepository.findById(user1Id).orElse(null);
                AuthenticationUser user2 = userRepository.findById(user2Id).orElse(null);
                
                if (user1 != null && user2 != null) {
                    String message = user1.getUsername() + " added you as a friend!";
                    notificationService.createNotification(user2, user1, "friend_request", message);
                }
            } catch (Exception notificationError) {
                // Log notification error but don't fail the friendship creation
                System.err.println("Error creating notification: " + notificationError.getMessage());
            }
            
            return ResponseEntity.ok(friendship);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error adding friend: " + e.getMessage());
        }
    }

    /**
     * Remove a friendship between two users.
     * 
     * @param request should contain user1_id and user2_id
     * @return success message
     */
    @PostMapping("/remove")
    public ResponseEntity<?> removeFriend(@RequestBody Map<String, Long> request) {
        try {
            Long user1Id = request.get("user1_id");
            Long user2Id = request.get("user2_id");

            if (user1Id == null || user2Id == null) {
                return ResponseEntity.badRequest().body("Missing user1_id or user2_id");
            }

            friendshipService.removeFriend(user1Id, user2Id);
            return ResponseEntity.ok("Friendship removed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error removing friend: " + e.getMessage());
        }
    }

    /**
     * Check if two users are friends.
     * 
     * @param user1Id the ID of the first user
     * @param user2Id the ID of the second user
     * @return true/false based on friendship status
     */
    @GetMapping("/check/{user1Id}/{user2Id}")
    public ResponseEntity<?> areFriends(@PathVariable Long user1Id, @PathVariable Long user2Id) {
        try {
            boolean areFriends = friendshipService.areFriends(user1Id, user2Id);
            return ResponseEntity.ok(Map.of("areFriends", areFriends));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error checking friendship: " + e.getMessage());
        }
    }

    /**
     * Get all friendships of a user.
     * 
     * @param userId the user ID
     * @return list of friendships
     */
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
