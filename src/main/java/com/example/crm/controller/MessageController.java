package com.example.crm.controller;

import com.example.crm.Dto.MessagesDto;
import com.example.crm.Dto.TypingDto;
import com.example.crm.service.MessagesService;
import com.example.crm.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class MessageController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private MessagesService service;
    @Autowired
    private NotificationService notificationService;

    @MessageMapping("/api/chat")
    public void sendMessage(MessagesDto message) {
        try {
            service.saveMessages(message, message.getSender());
        } catch (Exception ignored) { }
        messagingTemplate.convertAndSendToUser(message.getSender(), "/queue/chat", message);
        messagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/chat", message);
        // Push real-time notification to the receiver
        notificationService.sendNotification(
                message.getReceiver(),
                "NEW_MESSAGE",
                "New message from " + message.getSender()
        );
    }

    // ── Team / group channel broadcast ────────────────────────────────────────
    @MessageMapping("/team/{teamId}")
    public void sendTeamMessage(@DestinationVariable String teamId, MessagesDto message) {
        messagingTemplate.convertAndSend("/topic/team/" + teamId, message);
    }

    @GetMapping("/api/chat/history")
    public ResponseEntity<?> getChatHistory(
            @RequestParam("user1") String user1,
            @RequestParam("user2") String user2) {
        try {
            return service.getChatHistoryService(user1, user2);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @MessageMapping("/msgDelivered")
    public void msgDelivered(MessagesDto msg) {
        try {
            service.msgDelivered(msg);
            msg.setStatus(com.example.crm.Enum.MessageStatus.DELIVERED);
            messagingTemplate.convertAndSendToUser(msg.getSender(), "/queue/messageStatus", msg);
        } catch (Exception ignored) { }
    }
    @MessageMapping("/msgRead")
    public void msgRead(MessagesDto msg) {
        try {
            service.msgRead(msg);
            msg.setStatus(com.example.crm.Enum.MessageStatus.READ);
            messagingTemplate.convertAndSendToUser(msg.getSender(), "/queue/messageStatus", msg);
        } catch (Exception ignored) { }
    }
    @MessageMapping("/typing")
    public void typing(TypingDto dto){
        messagingTemplate.convertAndSendToUser(
                dto.getReceiver(),
                "/queue/typing",
                dto
        );
    }
}
