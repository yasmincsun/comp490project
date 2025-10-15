package main.java.com.musicApp.backend.features.databasemodel;

import com.musicApp.backend.features.user.model.User;
import com.musicApp.backend.features.song.model.Song;
import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "playlists")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer playlistID;

    // Reference user by username (foreign key)
    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = true)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('public','private') DEFAULT 'public'")
    private Privacy privacy = Privacy.PUBLIC;

    private LocalTime runtime;

    // Songs in this playlist (join table)
    @ManyToMany
    @JoinTable(
        name = "playlist_songs",
        joinColumns = @JoinColumn(name = "playlistID"),
        inverseJoinColumns = @JoinColumn(name = "songID")
    )
    private List<Song> songs;

    public enum Privacy {
        PUBLIC,
        PRIVATE
    }

    // Getters and setters
    // ...
}
