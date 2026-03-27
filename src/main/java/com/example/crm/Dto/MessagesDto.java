package com.example.crm.Dto;

import com.example.crm.Enum.MessageStatus;
import com.example.crm.Model.MessageModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MessagesDto {
    private Long id;
    private String sender;
    private String receiver;
    private String content;
    private LocalDateTime timestamp;
    private MessageStatus status;
    private Long employeeId;

    public MessagesDto(MessageModel data) {
        this.id = data.getId();
        this.content = data.getContent();
        this.sender = data.getSender();
        this.receiver = data.getReceiver();
        this.timestamp = data.getTimestamp();
        this.status = data.getStatus();
        if (data.getEmployeeId() != null) {
            this.employeeId = data.getEmployeeId().getId();
        }
    }
}
