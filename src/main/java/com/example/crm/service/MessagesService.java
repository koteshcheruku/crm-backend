package com.example.crm.service;

import com.example.crm.Dto.MessagesDto;
import com.example.crm.Enum.MessageStatus;
import com.example.crm.Model.MessageModel;
import com.example.crm.Repo.MessagesRepo;
import com.example.crm.Repo.UsersRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessagesService {
    @Autowired
    private MessagesRepo repo;
    @Autowired
    private UsersRepo usersRepo;

    @Transactional
    public void saveMessages(MessagesDto data, String senderUsername) {
        System.out.println("Message saved : " + data.getContent());
        MessageModel msg = new MessageModel();
        usersRepo.findByUsername(senderUsername).ifPresent(msg::setEmployeeId);
        msg.setStatus(MessageStatus.SENT);
        msg.setContent(data.getContent());
        msg.setReceiver(data.getReceiver());
        msg.setSender(data.getSender());
        repo.save(msg);
    }

    @Transactional
    public ResponseEntity<?> getChatHistoryService(String user1, String user2) {
        List<MessageModel> data = repo.findBySenderAndReceiverOrReceiverAndSenderOrderByIdAsc(
                user1, user2, user1, user2);
        List<MessagesDto> result = data.stream().map(MessagesDto::new).toList();
        System.out.println("getChatHistoryService: " + result);
        System.out.println("getChatHistoryService data: " + data);
        return ResponseEntity.ok(result);
    }

    @Transactional
    public void msgDelivered(MessagesDto message) {
        repo.findTopBySenderAndReceiverOrderByIdDesc(message.getSender(), message.getReceiver()).ifPresent(m -> {
            m.setStatus(MessageStatus.DELIVERED);
            repo.save(m);
        });
    }

    @Transactional
    public void msgRead(MessagesDto message) {
        repo.findTopBySenderAndReceiverOrderByIdDesc(message.getSender(), message.getReceiver()).ifPresent(m -> {
            m.setStatus(MessageStatus.READ);
            repo.save(m);
        });
    }
}
