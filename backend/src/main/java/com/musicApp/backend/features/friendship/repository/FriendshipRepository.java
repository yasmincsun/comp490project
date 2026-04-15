package com.musicApp.backend.features.friendship.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.musicApp.backend.features.friendship.model.Friendship;
import com.musicApp.backend.features.friendship.model.FriendshipId;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Friendship persistence operations.
 * Provides Spring Data JPA queries for reading and checking friendship relationships.
 * @author Yasmin Zubair
 * Date: April 15th, 2026
 */
@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipId> {
    
    /**
     * Find a friendship between two users.
     */
    Optional<Friendship> findByUser1AndUser2(AuthenticationUser user1, AuthenticationUser user2);
    
    /**
     * Find all friendships where a user is user1 (initiator).
     */
    List<Friendship> findByUser1(AuthenticationUser user1);
    
    /**
     * Find all friendships where a user is user2 (recipient).
     */
    List<Friendship> findByUser2(AuthenticationUser user2);

    /**
     * Find all friendships where a user is user1 and the friendship has a specific status.
     */
    List<Friendship> findByUser1AndStatus(AuthenticationUser user1, String status);

    /**
     * Find all friendships where a user is user2 and the friendship has a specific status.
     */
    List<Friendship> findByUser2AndStatus(AuthenticationUser user2, String status);

    /**
     * Find accepted friendships for a user in either direction.
     */
    List<Friendship> findByUser1_IdAndStatusOrUser2_IdAndStatus(Long userId1, String status1, Long userId2, String status2);
    
    /**
     * Check if a friendship exists from user1 to user2.
     */
    boolean existsByUser1_IdAndUser2_Id(Long user1Id, Long user2Id);

    /**
     * Check if a friendship exists from user2 to user1.
     */
    boolean existsByUser2_IdAndUser1_Id(Long user2Id, Long user1Id);

    /**
     * Check if a pending friendship exists from user1 to user2.
     */
    boolean existsByUser1_IdAndUser2_IdAndStatus(Long user1Id, Long user2Id, String status);

    /**
     * Check if a pending friendship exists from user2 to user1.
     */
    boolean existsByUser2_IdAndUser1_IdAndStatus(Long user2Id, Long user1Id, String status);

    /**
     * Find a friendship in the reverse direction.
     */
    Optional<Friendship> findByUser2AndUser1(AuthenticationUser user2, AuthenticationUser user1);

    /**
     * Find a friendship in the reverse direction with a specific status.
     */
    Optional<Friendship> findByUser2AndUser1AndStatus(AuthenticationUser user2, AuthenticationUser user1, String status);

    /**
     * Find a friendship from user1 to user2 with a specific status.
     */
    Optional<Friendship> findByUser1AndUser2AndStatus(AuthenticationUser user1, AuthenticationUser user2, String status);
}

