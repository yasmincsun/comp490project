package com.musicApp.backend.features.databasemodel;

import com.musicApp.backend.features.authentication.model.AuthenticationUser;
// import com.musicApp.backend.features.databasemodel.User;
import com.musicApp.backend.features.databasemodel.Song;
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entity class for Review.
 * Defines the review data model used for storing user reviews in the database.
 * Includes relationships to the user and optional song entity, rating metadata, and review content.
 * @author Yasmin Zubair
 * Date: April 15th, 2026
 */

@Entity
@Table(name = "reviews")
public class Review {

    /**
     * Primary identifier for the review.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reviewID;

    /**
     * User who created the review.
     */
    @ManyToOne
    @JoinColumn(name = "userID", referencedColumnName = "id", nullable = true)
    private AuthenticationUser user;

    /**
     * Optional song associated with the review.
     */
    @ManyToOne
    @JoinColumn(name = "songID", nullable = true)
    private Song song;

    /**
     * Album name for the reviewed item.
     */
    @Column(length = 100)
    private String album;

    /**
     * Type of target being reviewed (track, artist, album, etc.).
     */
    @Column(length = 50)
    private String targetType;

    /**
     * Name of the target entity being reviewed.
     */
    @Column(length = 100)
    private String targetName;

    /**
     * Artist name associated with the review target.
     */
    @Column(length = 100)
    private String artist;

    /**
     * Rating assigned by the user.
     */
    @Column
    private Integer rating;

    /**
     * Date when the review was posted.
     */
    @Column
    private LocalDate datePosted;

    /**
     * Comment text for the review.
     */
    @Column(columnDefinition = "TEXT")
    private String comment;

    public Review() {
    }

    public Integer getReviewID() {
        return reviewID;
    }

    public AuthenticationUser getUser() {
        return user;
    }

    public void setUser(AuthenticationUser user) {
        this.user = user;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDate getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(LocalDate datePosted) {
        this.datePosted = datePosted;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
