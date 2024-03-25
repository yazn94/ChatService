package com.chat.chatservice.service;

import com.chat.chatservice.model.ChatMessage;
import com.chat.chatservice.model.MessageDTO;
import com.chat.chatservice.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.sql.Timestamp;

@Service
public class ChatService {
    private final WebSocketSessionManager webSocketSessionManager;
    private final MessageQueue messageQueue;
    private final ObjectMapper objectMapper;

    @Autowired
    public ChatService(WebSocketSessionManager webSocketSessionManager, MessageQueue messageQueue, ObjectMapper objectMapper) {
        this.webSocketSessionManager = webSocketSessionManager;
        this.messageQueue = messageQueue;
        this.objectMapper = objectMapper;
    }

    public void handleNewUserConnection(String token, WebSocketSession session) throws IOException {
        String email = JwtTokenUtil.getEmailFromToken(token);
        System.out.println("Added user with email: " + email);
        webSocketSessionManager.addUserWebSocketSession(email, session);
        session.getAttributes().put("email", email);
        sendQueuedMessages(email);
    }

    public void handleDisconnection(WebSocketSession session) {
        if (session.getAttributes().get("email") == null) {
            return;
        }
        webSocketSessionManager.removeUserWebSocketSession(session);
    }

    public boolean isAuthenticatedUser(String token, WebSocketSession session) {
        String email = JwtTokenUtil.getEmailFromToken(token);
        System.out.println("Checking if user is authenticated: " + email + " " + session);
        return webSocketSessionManager.containsUserWebSocketSession(email) &&
                webSocketSessionManager.getUserWebSocketSession(email).equals(session) &&
                session.getAttributes().get("email").equals(email);
    }

    public void sendMessage(MessageDTO messageDTO) throws IOException {
        System.out.println("sending message: " + messageDTO.getContent() + " to " + messageDTO.getReceiverEmail());
        ChatMessage chatMessage = ChatMessage.builder()
                .content(messageDTO.getContent())
                .senderEmail(JwtTokenUtil.getEmailFromToken(messageDTO.getToken()))
                .receiverEmail(messageDTO.getReceiverEmail())
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();
        System.out.println("created chat message: " + chatMessage);

        if (webSocketSessionManager.containsUserWebSocketSession(chatMessage.getReceiverEmail())) {
            String chatMessageJson = objectMapper.writeValueAsString(chatMessage);
            WebSocketSession receiverSession = webSocketSessionManager.getUserWebSocketSession(chatMessage.getReceiverEmail());
            receiverSession.sendMessage(new TextMessage(chatMessageJson));
        } else {
            System.out.println("receiver is offline, adding message to queue");
            messageQueue.addMessage(chatMessage.getReceiverEmail(), chatMessage);
        }
    }

    private void sendQueuedMessages(String email) throws IOException {
        while (messageQueue.hasMessages(email)) {
            ChatMessage chatMessage = messageQueue.getMessageAndRemove(email);
            System.out.println("sending queued message: " + chatMessage);
            String chatMessageJson = objectMapper.writeValueAsString(chatMessage);
            WebSocketSession receiverSession = webSocketSessionManager.getUserWebSocketSession(chatMessage.getReceiverEmail());
            receiverSession.sendMessage(new TextMessage(chatMessageJson));
        }
    }
}