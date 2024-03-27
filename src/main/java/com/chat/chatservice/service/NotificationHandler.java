package com.chat.chatservice.service;

import com.chat.chatservice.exceptions.EmailSendingException;
import com.chat.chatservice.model.MessageDTO;
import com.chat.chatservice.util.JwtTokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationHandler {
    private final EmailSender emailService;

    public void composeMessageNotification(MessageDTO messageDTO) throws EmailSendingException {
        String receiverEmail = messageDTO.getReceiverEmail();
        String senderEmail = JwtTokenUtil.getEmailFromToken(messageDTO.getToken());
        String senderUsername = JwtTokenUtil.getUsernameFromToken(messageDTO.getToken());
        String messageContent = "You have a new message from " + senderUsername + " (" + senderEmail + ")!";
        emailService.sendEmail(receiverEmail, "New Message Notification", messageContent);
    }
}
