package com.musicApp.backend.features.databasemodel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByUser_Id(Long userId);

    @Query("SELECT r FROM Review r WHERE r.user.id IN :userIds ORDER BY r.datePosted DESC")
    List<Review> findByUserIdsOrderByDatePostedDesc(@Param("userIds") List<Long> userIds);

    @Query("SELECT r FROM Review r LEFT JOIN r.song s WHERE " +
           "LOWER(r.targetName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.artist) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.album) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.artist) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Review> searchByQuery(@Param("query") String query);
}
