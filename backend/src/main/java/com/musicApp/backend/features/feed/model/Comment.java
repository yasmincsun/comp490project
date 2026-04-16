/**
 * Class Name: Comment
 * Date: February 13, 2026
 * @author Jose Bastidas
 *
 */
package com.musicApp.backend.features.feed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * This class represents a comment made on a post in the feed.
 * It stores the comment id, the related post, the author of the
 * comment, the comment content, and the creation and update dates.
 */
@Entity(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    private Post post;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private AuthenticationUser author;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    private LocalDateTime creationDate;

    private LocalDateTime updatedDate;

    /**
     * Creates an empty Comment object.
     */
    public Comment() {
    }

    /**
     * Creates a Comment object with a post, author, and content.
     *
     * @param post the post associated with the comment
     * @param author the user who created the comment
     * @param content the text content of the comment
     */
    public Comment(Post post, AuthenticationUser author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
    }

    /**
     * Updates the modified date before the comment is updated.
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

    /**
     * Returns the id of the comment.
     *
     * @return the comment id
     */
    public Long getId() {
        return id;
    }

    /**
     * Updates the id of the comment.
     *
     * @param id the new comment id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the post associated with the comment.
     *
     * @return the {@link Post} associated with the comment
     */
    public Post getPost() {
        return post;
    }

    /**
     * Updates the post associated with the comment.
     *
     * @param post the new post associated with the comment
     */
    public void setPost(Post post) {
        this.post = post;
    }

    /**
     * Returns the author of the comment.
     *
     * @return the {@link AuthenticationUser} who created the comment
     */
    public AuthenticationUser getAuthor() {
        return author;
    }

    /**
     * Updates the author of the comment.
     *
     * @param author the new author of the comment
     */
    public void setAuthor(AuthenticationUser author) {
        this.author = author;
    }

    /**
     * Returns the content of the comment.
     *
     * @return the text content of the comment
     */
    public String getContent() {
        return content;
    }

    /**
     * Updates the content of the comment.
     *
     * @param content the new text content of the comment
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Returns the creation date of the comment.
     *
     * @return the date and time the comment was created
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Updates the creation date of the comment.
     *
     * @param creationDate the new creation date
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Returns the last updated date of the comment.
     *
     * @return the date and time the comment was last updated
     */
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    /**
     * Updates the last updated date of the comment.
     *
     * @param updatedDate the new updated date
     */
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}