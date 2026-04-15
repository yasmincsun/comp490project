package com.musicApp.backend.features.databasemodel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Review persistence operations.
 * Supports retrieving user reviews, searching reviews, and loading feeds of friend reviews.
 * @author Yasmin Zubair
 * Date: April 15th, 2026
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    /**
     * Find all reviews written by a user.
     * @param userId id of the user whose reviews should be returned
     * @return list of Review objects for the user
     */
    List<Review> findByUser_Id(Long userId);

    /**
     * Find recent reviews written by a list of users.
     * @param userIds list of user ids to search for
     * @return list of Review objects ordered by datePosted descending
     */
    @Query("SELECT r FROM Review r WHERE r.user.id IN :userIds ORDER BY r.datePosted DESC")
    List<Review> findByUserIdsOrderByDatePostedDesc(@Param("userIds") List<Long> userIds);

    /**
     * Search reviews by query text matching target name, artist, album, or song metadata.
     * @param query search string to compare against review and song fields
     * @return list of matching Review objects
     */
    @Query("SELECT r FROM Review r LEFT JOIN r.song s WHERE " +
           "LOWER(r.targetName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.artist) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.album) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.artist) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Review> searchByQuery(@Param("query") String query);
}
