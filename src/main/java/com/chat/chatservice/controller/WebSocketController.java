package com.chat.chatservice.controller;

import com.chat.chatservice.model.MessageDTO;
import com.chat.chatservice.model.MessageType;
import com.chat.chatservice.service.ChatService;
import com.chat.chatservice.service.NotificationHandler;
import com.chat.chatservice.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Controller
@AllArgsConstructor
public class WebSocketController extends TextWebSocketHandler {
    private final ChatService chatService;
    private final NotificationHandler notificationHandler;

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
