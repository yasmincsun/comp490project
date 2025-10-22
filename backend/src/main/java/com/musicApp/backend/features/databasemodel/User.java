package com.musicApp.backend.features.databasemodel;

import jakarta.persistence.*;
import java.util.List;
//Entity class for the creation of the users table

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"username"}),
    @UniqueConstraint(columnNames = {"email"})
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userID;

    @Column(nullable = false, length = 50)
    private String fname;

    @Column(nullable = false, length = 50)
    private String lname;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(length = 15)
    private String phoneNumber;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean loginStatus = false;

    @ManyToOne
    @JoinColumn(name = "current_moodID")
    private Mood currentMood;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer playlistCount = 0;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer friendsCount = 0;

    // Playlists owned by this user (mapped by username)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Playlist> playlists;

    // Reviews written by this user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews;

    


    // Getters and setters
        public void setUsername(String username) {
        this.username = username;
    }
        public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;   
    }

    public String getPassword() {
        return password;
    }   

    public void setCurrentMood(Mood currentMood) {
        this.currentMood = currentMood;  
    }
    public Mood getCurrentMood() {
        return currentMood;
    }
}
