package com.musicApp.backend.features.databasemodel;

import com.musicApp.backend.features.authentication.model.AuthenticationUser;
// import com.musicApp.backend.features.databasemodel.User;
import com.musicApp.backend.features.databasemodel.Song;
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entity class for Review
 * Uses JPA  to define the entity and its relationships. It creates a table named "reviews" with fields 
 * for reviewID, userID, songID, album, rating, datePosted, and comment.
 * @author M. Alfaro

 */

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reviewID;

    @ManyToOne
    @JoinColumn(name = "userID", referencedColumnName = "id", nullable = true)
    private AuthenticationUser user;


    @ManyToOne
    @JoinColumn(name = "songID", nullable = true)
    private Song song;

    @Column(length = 100)
    private String album;

    @Column(length = 50)
    private String targetType;

    @Column(length = 100)
    private String targetName;

    @Column(length = 100)
    private String artist;

    @Column
    private Integer rating;

    @Column
    private LocalDate datePosted;

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
