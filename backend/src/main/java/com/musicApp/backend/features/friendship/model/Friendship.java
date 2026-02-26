package com.musicApp.backend.features.friendship.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * Friendship entity that represents a friendship relationship between two users.
 * A friendship is directional: user1 adds user2 as a friend.
 */
@Entity
@Table(name = "friendship")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    private AuthenticationUser user1;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    private AuthenticationUser user2;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Constructors
    public Friendship() {
    }

    public Friendship(AuthenticationUser user1, AuthenticationUser user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
}
