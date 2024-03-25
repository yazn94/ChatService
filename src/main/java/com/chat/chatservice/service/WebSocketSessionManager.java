package com.chat.chatservice.service;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;

@Component
public class WebSocketSessionManager {
    private final HashMap<String, WebSocketSession> userSessionMap = new HashMap<>();

    public void addUserWebSocketSession(String userEmail, WebSocketSession session) {
        userSessionMap.put(userEmail, session);
    }

    public boolean containsUserWebSocketSession(String userEmail) {
        return userSessionMap.containsKey(userEmail);
    }

    public void removeUserWebSocketSession(WebSocketSession webSocketSession) {
        String email = (String) webSocketSession.getAttributes().get("email");
        userSessionMap.remove(email);
    }

    public WebSocketSession getUserWebSocketSession(String userEmail) {
        return userSessionMap.get(userEmail);
    }
}