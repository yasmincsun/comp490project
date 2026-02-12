//Post.java
// This class creates a Post entity that will be used in the database
//Author: Miguel A.

package com.musicApp.backend.features.databasemodel;

import java.time.LocalDateTime;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "posts") //Creates a table named "posts" in the database
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postID;

    @Column(nullable = false, length = 50) //Creates column for title with a maximum length of 50 characters and cannot be null
    private String postTitle;

    @Column(nullable = false, columnDefinition = "TEXT", length = 200) //Creates column for post content with a data type of TEXT
    private String postContent;

    @Column(nullable = false, length = 20) //Creates column for category, max 20 characters and cannot be null
    private String postCategory;

    @Column(nullable = true) //Creates column for Event date, can be null
    private LocalDateTime postEventDate;

    @Column(nullable = false) //Creates column for post date, cannot be null
    private LocalDateTime postCreated = LocalDateTime.now();

    @Column(nullable = true) //Location placeholder, can be null
    private String postLocation;

    // Getters and setters go below

}
