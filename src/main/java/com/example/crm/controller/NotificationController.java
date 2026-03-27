package com.example.crm.controller;

import com.example.crm.Dto.UserPrincipal;
import com.example.crm.Model.NotificationModel;
import com.example.crm.Repo.NotificationsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationsRepo notificationsRepo;

    @GetMapping("")
    public ResponseEntity<?> getNotifications(Authentication authentication) {
        try {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            String username = principal.getEmployee().getUsername();
            List<NotificationModel> notifications =
                    notificationsRepo.findTop50ByRecipientUsernameOrderByCreatedAtDesc(username);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch notifications");
        }
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable Long id, Authentication authentication) {
        try {
            NotificationModel notification = notificationsRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            String username = principal.getEmployee().getUsername();
            if (!username.equals(notification.getRecipientUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed");
            }
            notification.setIsRead(true);
            notificationsRepo.save(notification);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to mark notification as read");
        }
    }

    @PatchMapping("/read-all")
    public ResponseEntity<?> markAllRead(Authentication authentication) {
        try {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            String username = principal.getEmployee().getUsername();
            List<NotificationModel> batch = notificationsRepo
                    .findTop50ByRecipientUsernameOrderByCreatedAtDesc(username);

            batch.stream().filter(n -> !Boolean.TRUE.equals(n.getIsRead())).forEach(n -> n.setIsRead(true));
            notificationsRepo.saveAll(batch);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to mark all notifications as read");
        }
    }
}
