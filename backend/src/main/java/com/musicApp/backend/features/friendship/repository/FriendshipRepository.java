package com.musicApp.backend.features.friendship.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * Check if a friendship exists between two users in either direction using native query.
     *
     * Fix Long-to-Boolean cast issue by returning explicit boolean values instead of a raw numeric expression.
     */
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM friendship WHERE (user1_id = :user1Id AND user2_id = :user2Id) OR (user1_id = :user2Id AND user2_id = :user1Id)", nativeQuery = true)
    boolean existsByUser1AndUser2OrUser2AndUser1(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}
