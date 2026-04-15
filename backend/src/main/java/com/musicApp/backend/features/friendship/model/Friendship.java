package com.musicApp.backend.features.friendship.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * Friendship entity that represents a friendship relationship between two users.
 * The friendship primary key is the pair of users, matching a schema without a separate id column.
 * Contains the creation timestamp and the current friendship status.
 * @author Yasmin Zubair
 * Date: April 15th, 2026
 */
@Entity
@IdClass(FriendshipId.class)
@Table(name = "friendship")
public class Friendship {

    @Id
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    private AuthenticationUser user1;

    @Id
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    private AuthenticationUser user2;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "status", nullable = false, length = 20)
    private String status = STATUS_PENDING;

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_ACCEPTED = "ACCEPTED";

    // Constructors
    public Friendship() {
    }

    public Friendship(AuthenticationUser user1, AuthenticationUser user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    // Getters and Setters
    public AuthenticationUser getUser1() {
        return user1;
    }

    public void setUser1(AuthenticationUser user1) {
        this.user1 = user1;
    }

    public AuthenticationUser getUser2() {
        return user2;
    }

    public void setUser2(AuthenticationUser user2) {
        this.user2 = user2;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
