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

    @Column
    private Integer rating;

    @Column
    private LocalDate datePosted;

    @Column(columnDefinition = "TEXT")
    private String comment;

    // Getters and setters
    // ...
}
