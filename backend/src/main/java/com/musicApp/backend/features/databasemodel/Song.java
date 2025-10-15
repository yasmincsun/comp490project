package main.java.com.musicApp.backend.features.databasemodel;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "songs")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer songID;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 100)
    private String artist;

    @Column(length = 100)
    private String album;

    private LocalTime length;

    private LocalDate releaseDate;

    @Column(length = 50)
    private String genre;

    @ManyToOne
    @JoinColumn(name = "moodID")
    private Mood mood;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer playCount = 0;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer views = 0;

    private LocalDateTime lastPlayed;

    // Playlists this song belongs to
    @ManyToMany(mappedBy = "songs")
    private List<Playlist> playlists;

    // Reviews of this song
    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL)
    private List<Review> reviews;

    // Getters and setters
    // ...
}

