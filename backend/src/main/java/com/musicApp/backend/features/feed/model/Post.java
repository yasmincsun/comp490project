/**
 * Date: March 27, 2026
 * @author Miguel Alfaro
 *
 */
package com.musicApp.backend.features.feed.model;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotEmpty;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;

/**
 * Class representing a Post entity in the MusicApp backend. Each post contains content, 
 * an optional picture, an author (linked to AuthenticationUser), timestamps for creation and updates, and relationships to comments and likes.
 *  This class is mapped to the "posts" table in the database using JPA annotations.
 */

/**
 * Post entity.
 */
@Entity(name = "posts")
public class Post {

    /**
     * JPA constructor.
     */
    public Post() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String content;

    private String picture;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private AuthenticationUser author;

    @CreationTimestamp
    private LocalDateTime creationDate;

    private LocalDateTime updatedDate;

    @OneToMany(
        mappedBy = "post",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Comment> comments;

    @ManyToMany
    @JoinTable(
        name = "posts_likes",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<AuthenticationUser> likes;

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

    /**
     * Creates a post.
     *
     * @param content post content
     * @param author post author
     */
    public Post(String content, AuthenticationUser author) {
        this.content = content;
        this.author = author;
    }

    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public AuthenticationUser getAuthor() { return author; }
    public void setAuthor(AuthenticationUser author) { this.author = author; }

    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }

    public Set<AuthenticationUser> getLikes() { return likes; }
    public void setLikes(Set<AuthenticationUser> likes) { this.likes = likes; }
}