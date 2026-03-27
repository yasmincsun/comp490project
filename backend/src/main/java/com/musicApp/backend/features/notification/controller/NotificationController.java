package com.musicApp.backend.features.notification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.musicApp.backend.features.notification.model.Notification;
import com.musicApp.backend.features.notification.service.NotificationService;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Get all notifications for the current user.
     */
    @GetMapping
    public ResponseEntity<?> getNotifications(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        try {
            List<Notification> notifications = notificationService.getNotifications(user);
            List<NotificationDTO> dtos = notifications.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching notifications: " + e.getMessage());
        }
    }

    /**
     * Get unread notifications for the current user.
     */
    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadNotifications(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        try {
            List<Notification> notifications = notificationService.getUnreadNotifications(user);
            List<NotificationDTO> dtos = notifications.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching unread notifications: " + e.getMessage());
        }
    }

    /**
     * Mark a notification as read.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            Notification notification = notificationService.markAsRead(id);
            return ResponseEntity.ok(toDTO(notification));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error marking notification as read: " + e.getMessage());
        }
    }

    /**
     * Delete a notification.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok("Notification deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting notification: " + e.getMessage());
        }
    }

    // DTO class
    public static class NotificationDTO {
        public Long id;
        public String senderUsername;
        public String type;
        public String message;
        public Boolean isRead;
        public String createdAt;

        public NotificationDTO(Long id, String senderUsername, String type, String message, Boolean isRead, String createdAt) {
            this.id = id;
            this.senderUsername = senderUsername;
            this.type = type;
            this.message = message;
            this.isRead = isRead;
            this.createdAt = createdAt;
        }
    }

    private NotificationDTO toDTO(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getSender().getUsername(),
                notification.getType(),
                notification.getMessage(),
                notification.getIsRead(),
                notification.getCreatedAt().toString()
        );
    }
}
