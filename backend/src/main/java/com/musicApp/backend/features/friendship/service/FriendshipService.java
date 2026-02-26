package com.musicApp.backend.features.friendship.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.musicApp.backend.features.friendship.model.Friendship;
import com.musicApp.backend.features.friendship.repository.FriendshipRepository;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;
import java.util.List;
import java.util.Optional;

@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private AuthenticationUserRepository userRepository;

    /**
     * Add a user as a friend (create a friendship record).
     * 
     * @param user1Id the ID of the user initiating the friendship
     * @param user2Id the ID of the user being added as a friend
     * @return the created Friendship entity
     * @throws IllegalArgumentException if user or friendship already exists
     */
    public Friendship addFriend(Long user1Id, Long user2Id) {
        if (user1Id.equals(user2Id)) {
            throw new IllegalArgumentException("A user cannot be their own friend");
        }

        AuthenticationUser user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new IllegalArgumentException("User 1 not found"));
        AuthenticationUser user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new IllegalArgumentException("User 2 not found"));

        // Check if friendship already exists in either direction
        if (friendshipRepository.existsByUser1AndUser2OrUser2AndUser1(user1, user2, user2, user1)) {
            throw new IllegalArgumentException("Friendship already exists between these users");
        }

        Friendship friendship = new Friendship(user1, user2);
        return friendshipRepository.save(friendship);
    }

    /**
     * Remove a friendship between two users.
     * 
     * @param user1Id the ID of the first user
     * @param user2Id the ID of the second user
     */
    public void removeFriend(Long user1Id, Long user2Id) {
        AuthenticationUser user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new IllegalArgumentException("User 1 not found"));
        AuthenticationUser user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new IllegalArgumentException("User 2 not found"));

        Optional<Friendship> friendship = friendshipRepository.findByUser1AndUser2(user1, user2);
        if (friendship.isPresent()) {
            friendshipRepository.delete(friendship.get());
        }
    }

    /**
     * Check if two users are friends (checks both directions).
     * 
     * @param user1Id the ID of the first user
     * @param user2Id the ID of the second user
     * @return true if they are friends, false otherwise
     */
    public boolean areFriends(Long user1Id, Long user2Id) {
        AuthenticationUser user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new IllegalArgumentException("User 1 not found"));
        AuthenticationUser user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new IllegalArgumentException("User 2 not found"));

        return friendshipRepository.existsByUser1AndUser2OrUser2AndUser1(user1, user2, user2, user1);
    }

    /**
     * Get all friends of a user (both directions combined).
     * 
     * @param userId the ID of the user
     * @return list of all friends
     */
    public List<Friendship> getFriendships(Long userId) {
        AuthenticationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Friendship> friendships = friendshipRepository.findByUser1(user);
        friendships.addAll(friendshipRepository.findByUser2(user));
        return friendships;
    }
}
