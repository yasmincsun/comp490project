package com.musicApp.backend.features.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.musicApp.backend.features.notification.model.Notification;
import com.musicApp.backend.features.notification.repository.NotificationRepository;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Create a new notification.
     */
    public Notification createNotification(AuthenticationUser recipient, AuthenticationUser sender, String type, String message) {
        Notification notification = new Notification(recipient, sender, type, message);
        return notificationRepository.save(notification);
    }

    /**
     * Get all notifications for a user.
     */
    public List<Notification> getNotifications(AuthenticationUser recipient) {
        return notificationRepository.findByRecipient(recipient);
    }

    /**
     * Get unread notifications for a user.
     */
    public List<Notification> getUnreadNotifications(AuthenticationUser recipient) {
        return notificationRepository.findByRecipientAndIsReadFalse(recipient);
    }

    /**
     * Get notifications of a specific type.
     */
    public List<Notification> getNotificationsByType(AuthenticationUser recipient, String type) {
        return notificationRepository.findByRecipientAndType(recipient, type);
    }

    /**
     * Mark a notification as read.
     */
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    /**
     * Delete a notification.
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
