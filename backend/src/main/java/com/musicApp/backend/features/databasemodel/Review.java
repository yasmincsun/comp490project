package main.java.com.musicApp.backend.features.databasemodel;

import com.musicApp.backend.features.user.model.User;
import com.musicApp.backend.features.song.model.Song;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reviewID;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = true)
    private User user;

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
