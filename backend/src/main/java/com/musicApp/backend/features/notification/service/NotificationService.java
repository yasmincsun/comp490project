package com.musicApp.backend.features.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.musicApp.backend.features.notification.model.Notification;
import com.musicApp.backend.features.notification.repository.NotificationRepository;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import java.util.List;

/**
 * Service layer for notification operations.
 * Manages creation, retrieval, marking read, and deletion of notifications.
 * @author Yasmin Zubair
 * Date: April 15th, 2026
 */
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Create a new notification.
     * @param recipient recipient user for the notification
     * @param sender user who triggered the notification
     * @param type notification type identifier
     * @param message human readable notification message
     * @return saved Notification object
     */
    public Notification createNotification(AuthenticationUser recipient, AuthenticationUser sender, String type, String message) {
        Notification notification = new Notification(recipient, sender, type, message);
        return notificationRepository.save(notification);
    }

    /**
     * Get all notifications for a recipient user.
     * @param recipient user whose notifications should be retrieved
     * @return list of Notification objects for the recipient
     */
    public List<Notification> getNotifications(AuthenticationUser recipient) {
        return notificationRepository.findByRecipient(recipient);
    }

    /**
     * Get unread notifications for a recipient user.
     * @param recipient user whose unread notifications should be retrieved
     * @return list of unread Notification objects
     */
    public List<Notification> getUnreadNotifications(AuthenticationUser recipient) {
        return notificationRepository.findByRecipientAndIsReadFalse(recipient);
    }

    /**
     * Get notifications for a user of a specific type.
     * @param recipient user whose notifications should be filtered
     * @param type notification type identifier
     * @return list of Notification objects matching the type
     */
    public List<Notification> getNotificationsByType(AuthenticationUser recipient, String type) {
        return notificationRepository.findByRecipientAndType(recipient, type);
    }

    /**
     * Mark a notification as read.
     * @param notificationId id of the notification to mark as read
     * @return updated Notification object
     */
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    /**
     * Mark all notifications for a recipient as read.
     * @param recipient user whose unread notifications should be marked read
     */
    public void markAllAsRead(AuthenticationUser recipient) {
        List<Notification> unread = notificationRepository.findByRecipientAndIsReadFalse(recipient);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    /**
     * Delete a notification by id.
     * @param notificationId id of the notification to delete
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
