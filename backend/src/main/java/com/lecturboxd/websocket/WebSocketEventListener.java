package com.lecturboxd.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * EN: Listens to WebSocket session connect/disconnect events for logging.
 * KA: უსმენს WebSocket სესიის დაკავშირების/გათიშვის მოვლენებს ლოგირებისთვის.
 */
@Component
public class WebSocketEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * EN: Injects messaging template (available for future presence broadcasts).
     * KA: ინჯექციას უკეთებს მესიჯინგის შაბლონს (ხელმისაწვდომია მომავალი presence ბროდკასტებისთვის).
     */
    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * EN: Logs when a WebSocket client successfully connects.
     * KA: ლოგავს, როცა WebSocket კლიენტი წარმატებით უკავშირდება.
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        LOGGER.info("WebSocket client connected with session ID: {}", sessionId);
    }

    /**
     * EN: Logs when a WebSocket client disconnects.
     * KA: ლოგავს, როცა WebSocket კლიენტი გათიშულია.
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        LOGGER.info("WebSocket client disconnected with session ID: {}", sessionId);
    }
}
