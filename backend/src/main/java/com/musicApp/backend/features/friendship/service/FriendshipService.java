package com.musicApp.backend.features.friendship.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.musicApp.backend.features.friendship.model.Friendship;
import com.musicApp.backend.features.friendship.repository.FriendshipRepository;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import com.musicApp.backend.features.authentication.repository.AuthenticationUserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for friendship operations.
 * Handles friend request creation, acceptance, decline, removal, and friendship queries.
 * Contains helper logic for matching friendship status in either direction.
 * @author Yasmin Zubair
 * Date: April 15th, 2026
 */
@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private AuthenticationUserRepository userRepository;

    /**
     * Retrieve a user by id or throw an exception if the user does not exist.
     * @param userId user id to look up
     * @param label descriptive label used in exception messages
     * @return found AuthenticationUser
     */
    private AuthenticationUser getUserOrThrow(Long userId, String label) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(label + " not found"));
    }

    /**
     * Check whether an accepted friendship exists between two users in either direction.
     * @param user1Id first user id
     * @param user2Id second user id
     * @return true if an accepted friendship exists
     */
    private boolean acceptedFriendshipExists(Long user1Id, Long user2Id) {
        return friendshipRepository.existsByUser1_IdAndUser2_IdAndStatus(user1Id, user2Id, Friendship.STATUS_ACCEPTED)
                || friendshipRepository.existsByUser2_IdAndUser1_IdAndStatus(user2Id, user1Id, Friendship.STATUS_ACCEPTED);
    }

    /**
     * Check whether a pending friendship exists between two users in either direction.
     * @param user1Id first user id
     * @param user2Id second user id
     * @return true if a pending friendship exists
     */
    private boolean pendingFriendshipExists(Long user1Id, Long user2Id) {
        return friendshipRepository.existsByUser1_IdAndUser2_IdAndStatus(user1Id, user2Id, Friendship.STATUS_PENDING)
                || friendshipRepository.existsByUser2_IdAndUser1_IdAndStatus(user2Id, user1Id, Friendship.STATUS_PENDING);
    }

    /**
     * Send a friend request or accept a pending incoming request.
     */
    public Friendship addFriend(Long user1Id, Long user2Id) {
        if (user1Id.equals(user2Id)) {
            throw new IllegalArgumentException("A user cannot be their own friend");
        }

        AuthenticationUser user1 = getUserOrThrow(user1Id, "User 1");
        AuthenticationUser user2 = getUserOrThrow(user2Id, "User 2");

        if (acceptedFriendshipExists(user1Id, user2Id)) {
            throw new IllegalArgumentException("Friendship already exists between these users");
        }

        if (friendshipRepository.existsByUser2_IdAndUser1_IdAndStatus(user2Id, user1Id, Friendship.STATUS_PENDING)) {
            Friendship existing = friendshipRepository.findByUser2AndUser1AndStatus(user2, user1, Friendship.STATUS_PENDING)
                    .orElseThrow(() -> new IllegalArgumentException("Pending friendship not found"));
            existing.setStatus(Friendship.STATUS_ACCEPTED);
            return friendshipRepository.save(existing);
        }

        if (friendshipRepository.existsByUser1_IdAndUser2_IdAndStatus(user1Id, user2Id, Friendship.STATUS_PENDING)) {
            throw new IllegalArgumentException("Friend request already sent");
        }

        Friendship friendship = new Friendship(user1, user2);
        friendship.setStatus(Friendship.STATUS_PENDING);
        return friendshipRepository.save(friendship);
    }

    /**
     * Accept a pending friend request.
     */
    public Friendship acceptFriendRequest(Long requesterId, Long recipientId) {
        AuthenticationUser requester = getUserOrThrow(requesterId, "Requester");
        AuthenticationUser recipient = getUserOrThrow(recipientId, "Recipient");

        Friendship friendship = friendshipRepository.findByUser1AndUser2AndStatus(requester, recipient, Friendship.STATUS_PENDING)
                .orElseThrow(() -> new IllegalArgumentException("Pending friend request not found"));
        friendship.setStatus(Friendship.STATUS_ACCEPTED);
        return friendshipRepository.save(friendship);
    }

    /**
     * Decline a pending friend request.
     */
    public void declineFriendRequest(Long requesterId, Long recipientId) {
        AuthenticationUser requester = getUserOrThrow(requesterId, "Requester");
        AuthenticationUser recipient = getUserOrThrow(recipientId, "Recipient");

        Optional<Friendship> friendship = friendshipRepository.findByUser1AndUser2AndStatus(requester, recipient, Friendship.STATUS_PENDING);
        if (friendship.isEmpty()) {
            throw new IllegalArgumentException("Pending friend request not found");
        }
        friendshipRepository.delete(friendship.get());
    }

    /**
     * Remove a friendship or pending request between two users.
     */
    public void removeFriend(Long user1Id, Long user2Id) {
        AuthenticationUser user1 = getUserOrThrow(user1Id, "User 1");
        AuthenticationUser user2 = getUserOrThrow(user2Id, "User 2");

        Optional<Friendship> friendship = friendshipRepository.findByUser1AndUser2(user1, user2);
        if (friendship.isEmpty()) {
            friendship = friendshipRepository.findByUser2AndUser1(user2, user1);
        }
        friendship.ifPresent(friendshipRepository::delete);
    }

    /**
     * Check if two users are friends (accepted in either direction).
     */
    public boolean areFriends(Long user1Id, Long user2Id) {
        getUserOrThrow(user1Id, "User 1");
        getUserOrThrow(user2Id, "User 2");

        return friendshipRepository.existsByUser1_IdAndUser2_IdAndStatus(user1Id, user2Id, Friendship.STATUS_ACCEPTED)
                || friendshipRepository.existsByUser2_IdAndUser1_IdAndStatus(user2Id, user1Id, Friendship.STATUS_ACCEPTED);
    }

    /**
     * Get all accepted friendships of a user.
     */
    public List<Friendship> getFriendships(Long userId) {
        AuthenticationUser user = getUserOrThrow(userId, "User");

        List<Friendship> friendships = friendshipRepository.findByUser1AndStatus(user, Friendship.STATUS_ACCEPTED);
        friendships.addAll(friendshipRepository.findByUser2AndStatus(user, Friendship.STATUS_ACCEPTED));
        return friendships;
    }

    public String getFriendshipStatus(Long user1Id, Long user2Id) {
        getUserOrThrow(user1Id, "User 1");
        getUserOrThrow(user2Id, "User 2");

        if (acceptedFriendshipExists(user1Id, user2Id)) {
            return "friends";
        }
        if (friendshipRepository.existsByUser1_IdAndUser2_IdAndStatus(user1Id, user2Id, Friendship.STATUS_PENDING)) {
            return "pending_outgoing";
        }
        if (friendshipRepository.existsByUser1_IdAndUser2_IdAndStatus(user2Id, user1Id, Friendship.STATUS_PENDING)) {
            return "pending_incoming";
        }
        return "none";
    }

    public List<Long> getAcceptedFriendIds(Long userId) {
        AuthenticationUser user = getUserOrThrow(userId, "User");
        return friendshipRepository.findByUser1_IdAndStatusOrUser2_IdAndStatus(userId, Friendship.STATUS_ACCEPTED, userId, Friendship.STATUS_ACCEPTED)
                .stream()
                .map(friendship -> friendship.getUser1().getId().equals(userId) ? friendship.getUser2().getId() : friendship.getUser1().getId())
                .distinct()
                .collect(Collectors.toList());
    }
}
