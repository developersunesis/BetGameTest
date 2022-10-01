package com.gameapp.test;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketSessionDecorator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class TestClientWebSocket extends WebSocketSessionDecorator {

    private final ArrayList<TextMessage> messages;

    public TestClientWebSocket(WebSocketSession session) {
        super(session);
        this.messages = new ArrayList<>();
    }

    @Override
    public void sendMessage(WebSocketMessage<?> message) throws IOException {
        super.sendMessage(message);
        messages.add((TextMessage) message);
    }

    @Override
    public String getId() {
        return UUID.randomUUID().toString();
    }

    public ArrayList<TextMessage> getMessages() {
        return messages;
    }

    public void clearMessages() {
        messages.clear();
    }
}
