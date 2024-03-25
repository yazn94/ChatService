package com.chat.chatservice.controller;

import com.chat.chatservice.model.MessageDTO;
import com.chat.chatservice.model.MessageType;
import com.chat.chatservice.service.ChatService;
import com.chat.chatservice.service.NotificationHandler;
import com.chat.chatservice.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Controller
public class WebSocketController extends TextWebSocketHandler {
    private final ChatService chatService;
    private final NotificationHandler notificationHandler;

    @Autowired
    public WebSocketController(ChatService chatService, NotificationHandler notificationHandler) {
        this.chatService = chatService;
        this.notificationHandler = notificationHandler;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MessageDTO messageDTO = mapper.readValue(message.getPayload(), MessageDTO.class);

        String token = messageDTO.getToken();
        if (token == null || !JwtTokenUtil.validateToken(token)) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        if (messageDTO.getType().equals(MessageType.CHAT)) {
            if (!chatService.isAuthenticatedUser(token, session)) {
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }
            chatService.sendMessage(messageDTO);
            notificationHandler.composeMessageNotification(messageDTO);
        } else if (messageDTO.getType().equals(MessageType.AUTH)) {
            System.out.println("New Auth Message");
            chatService.handleNewUserConnection(messageDTO.getToken(), session);
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            throw new IllegalAccessException("Invalid message type");
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        chatService.handleDisconnection(session);
    }
}

/*

{
    "content": "Hello, Wesam!",
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJlbWFpbCI6Inlhem55b3VzZWY5NEBnbWFpbC5jb20iLCJ1c2VybmFtZSI6Inlhem45NCIsInVzZXJUeXBlIjoiQ0hJTEQiLCJleHAiOjE3NDI2MDc3NzF9.k8opyi5pBuleZK7YeoCazPyTNJpCFSi6H0MWIk0aO6eO7RTN1E21VOyy-rsWG9cEuT_RueYdWv-b3wNmBENaOQ",
    "receiverEmail": "wesamalia22@gmail.com",
    "type": "CHAT"
}

{
     "token": "eyJhbGciOiJIUzUxMiJ9.eyJlbWFpbCI6Inlhem55b3VzZWY5NEBnbWFpbC5jb20iLCJ1c2VybmFtZSI6Inlhem45NCIsInVzZXJUeXBlIjoiQ0hJTEQiLCJleHAiOjE3NDI2MDc3NzF9.k8opyi5pBuleZK7YeoCazPyTNJpCFSi6H0MWIk0aO6eO7RTN1E21VOyy-rsWG9cEuT_RueYdWv-b3wNmBENaOQ",
     "type": "AUTH"
}

{
    "content": "Hi, there Yazan!",
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJlbWFpbCI6Indlc2FtYWxpYTIyQGdtYWlsLmNvbSIsInVzZXJuYW1lIjoid2VzYW0yMiIsInVzZXJUeXBlIjoiUEFSRU5UIiwiZXhwIjoxNzQyNjA5MTA0fQ.wQzfD2ilerORv3NuE6YX1JX5bYAh8t9xSc5W0PFm2uHbdasWYqjfzdWkkGSZZAOc2jZUTbL7Ok-9Rh5s7JdNLQ",
    "receiverEmail": "yaznyousef94@gmail.com",
    "type": "CHAT"
}

{
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJlbWFpbCI6Indlc2FtYWxpYTIyQGdtYWlsLmNvbSIsInVzZXJuYW1lIjoid2VzYW0yMiIsInVzZXJUeXBlIjoiUEFSRU5UIiwiZXhwIjoxNzQyNjA5MTA0fQ.wQzfD2ilerORv3NuE6YX1JX5bYAh8t9xSc5W0PFm2uHbdasWYqjfzdWkkGSZZAOc2jZUTbL7Ok-9Rh5s7JdNLQ",
     "type": "AUTH"
}

 */