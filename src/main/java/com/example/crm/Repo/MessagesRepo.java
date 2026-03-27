package com.example.crm.Repo;

import com.example.crm.Model.MessageModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessagesRepo extends JpaRepository<MessageModel,Long> {
    @Transactional
    List<MessageModel> findBySenderAndReceiverOrReceiverAndSenderOrderByIdAsc(String user1, String user2, String user11, String user21);

    Optional<MessageModel> findTopBySenderAndReceiverOrderByIdDesc(String sender, String receiver);
}
