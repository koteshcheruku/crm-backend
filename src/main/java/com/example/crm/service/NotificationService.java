package com.example.crm.service;

import com.example.crm.Model.NotificationModel;
import com.example.crm.Repo.NotificationsRepo;
import com.example.crm.Repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    @Autowired
    private NotificationsRepo notificationsRepo;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Push a real-time notification to a specific user and persist it.
     *
     * @param recipientUsername the username of the recipient
     * @param type              e.g. "NEW_LEAD" or "NEW_MESSAGE"
     * @param message           human-readable notification message
     */
    public void sendNotification(String recipientUsername, String type, String message) {
        NotificationModel notification = new NotificationModel();
        notification.setRecipientUsername(recipientUsername);
        notification.setType(type);
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationsRepo.save(notification);

        // Push via WebSocket so the UI updates in real-time
        messagingTemplate.convertAndSendToUser(
                recipientUsername,
                "/queue/notifications",
                notification
        );
    }

    /**
     * Notify all admins in the system.
     */
    public void notifyAllAdmins(String type, String message) {
        usersRepo.findAll().stream()
                .filter(u -> u.getRole() != null &&
                        u.getRole().getName().equalsIgnoreCase("ADMIN"))
                .forEach(admin ->
                        sendNotification(admin.getUsername(), type, message)
                );
    }
}
