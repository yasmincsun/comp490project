package com.musicApp.backend.features.friendship.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.musicApp.backend.features.friendship.model.Friendship;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    
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
     * Check if a friendship exists between two users in either direction.
     */
    boolean existsByUser1AndUser2OrUser2AndUser1(AuthenticationUser user1a, AuthenticationUser user2a, AuthenticationUser user1b, AuthenticationUser user2b);
}
