package com.musicApp.backend.features.notification.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * Notification entity for user notifications (friend requests, etc)
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private AuthenticationUser recipient; // User receiving the notification

    @NotNull
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private AuthenticationUser sender; // User sending the notification

    @NotNull
    @Column(length = 50)
    private String type; // "friend_request", "friend_accepted", etc

    @Column(length = 500)
    private String message;

    private Boolean isRead = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Constructors
    public Notification() {
    }

    public Notification(AuthenticationUser recipient, AuthenticationUser sender, String type, String message) {
        this.recipient = recipient;
        this.sender = sender;
        this.type = type;
        this.message = message;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuthenticationUser getRecipient() {
        return recipient;
    }

    public void setRecipient(AuthenticationUser recipient) {
        this.recipient = recipient;
    }

    public AuthenticationUser getSender() {
        return sender;
    }

    public void setSender(AuthenticationUser sender) {
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
