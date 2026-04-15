package com.musicApp.backend.features.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.musicApp.backend.features.notification.model.Notification;
import com.musicApp.backend.features.authentication.model.AuthenticationUser;
import java.util.List;

/**
 * Repository interface for Notification persistence operations.
 * Defines query methods for recipient-based and unread notification retrieval.
 * @author Yasmin Zubair
 * Date: April 15th, 2026
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find all notifications for a user.
     */
    List<Notification> findByRecipient(AuthenticationUser recipient);
    
    /**
     * Find unread notifications for a user.
     */
    List<Notification> findByRecipientAndIsReadFalse(AuthenticationUser recipient);
    
    /**
     * Find notifications by type.
     */
    List<Notification> findByRecipientAndType(AuthenticationUser recipient, String type);
}
