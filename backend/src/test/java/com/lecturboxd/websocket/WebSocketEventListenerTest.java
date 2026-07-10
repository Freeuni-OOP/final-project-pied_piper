package com.lecturboxd.websocket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketEventListenerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketEventListener listener;

    @Test
    void handlesConnectAndDisconnectWithoutThrowing() {
        StompHeaderAccessor connectAccessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        connectAccessor.setSessionId("sess-1");
        SessionConnectEvent connectedEvent = mock(SessionConnectEvent.class);
        when(connectedEvent.getMessage()).thenReturn(
                MessageBuilder.createMessage(new byte[0], connectAccessor.getMessageHeaders()));

        listener.handleWebSocketConnectListener(connectedEvent);

        StompHeaderAccessor disconnectAccessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);
        disconnectAccessor.setSessionId("sess-1");
        SessionDisconnectEvent disconnectEvent = mock(SessionDisconnectEvent.class);
        when(disconnectEvent.getMessage()).thenReturn(
                MessageBuilder.createMessage(new byte[0], disconnectAccessor.getMessageHeaders()));

        listener.handleWebSocketDisconnectListener(disconnectEvent);
    }
}
