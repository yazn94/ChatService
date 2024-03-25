package com.chat.chatservice.service;

import com.chat.chatservice.model.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Queue;
 
@Service
public class MessageQueue {
    private final HashMap<String, Queue<ChatMessage>> queue;

    public MessageQueue() {
        queue = new HashMap<>();
    }

    public void addMessage(String receiverEmail, ChatMessage message) {
        if (!queue.containsKey(receiverEmail)) {
            queue.put(receiverEmail, new java.util.LinkedList<>());
        }
        queue.get(receiverEmail).add(message);
    }

    public ChatMessage getMessageAndRemove(String receiverEmail) {
        return queue.get(receiverEmail).remove();
    }

    public boolean hasMessages(String receiverEmail) {
        return queue.containsKey(receiverEmail) &&
                !queue.get(receiverEmail).isEmpty();
    }
}
