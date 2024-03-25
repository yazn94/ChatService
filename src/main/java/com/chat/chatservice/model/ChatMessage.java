package com.chat.chatservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@AllArgsConstructor
@Builder
@Data
public class ChatMessage {
    private String content;
    private String senderEmail;
    private String receiverEmail;
    private Timestamp timestamp;
}
