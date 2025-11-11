package com.musicApp.backend.features.databasemodel;


import jakarta.persistence.*;
import java.util.List;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;

//Entity class for Mood
//Uses JPA  to define the entity and its relationships. It creates a table named "moods" with fields 
//for moodID, majorMood, and subCategory.
//Author: M. Alfaro
@Entity
@Table(name = "moods")
public class Mood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer moodID;

    @Column(nullable = false, length = 50)
    private String majorMood;

    @Column(length = 50)
    private String subCategory;

    // Bidirectional mapping to Users
    @OneToMany(mappedBy = "currentMood")
    private List<AuthenticationUser> users;

    // Bidirectional mapping to Songs
    @OneToMany(mappedBy = "mood")
    private List<Song> songs;



    // Getters and setters
    public void setMajorMood(String majorMood) {
        this.majorMood = majorMood;
    }
    public String getMajorMood() {
        return majorMood;
    }   
}
