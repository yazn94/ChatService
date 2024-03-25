package com.chat.chatservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.ExecutionException;

public class WsTest {

    @Test
    public void testWebSocket() throws ExecutionException, InterruptedException {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompHeaders headers = new StompHeaders();
        String token = "your_token_here";
        headers.add("Authorization", "Bearer " + token);

        String url = "ws://localhost:8081/chat";
        ListenableFuture<StompSession> sessionFuture = stompClient.connect(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                // Handle after connection logic if needed
            }
        }, headers);

        // Wait for the connection to be established
        StompSession session = sessionFuture.get();

        // Add your assertions or further test logic here
    }

}
