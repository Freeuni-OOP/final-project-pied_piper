package com.lecturboxd.config;

import com.lecturboxd.websocket.StompAuthChannelInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * EN: Enables STOMP-over-WebSocket messaging with broker prefixes, SockJS endpoint, and JWT interceptor.
 * KA: ჩართავს STOMP-over-WebSocket მესიჯინგს ბროკერის პრეფიქსებით, SockJS ენდპოინტით და JWT ინტერცეპტორით.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;

    @Value("${lecturboxd.cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    /**
     * EN: Injects the STOMP channel interceptor used to authenticate CONNECT frames.
     * KA: ინჯექციას უკეთებს STOMP არხის ინტერცეპტორს, რომელიც CONNECT ფრეიმებს ავთენტიფიცირებს.
     */
    public WebSocketConfig(StompAuthChannelInterceptor stompAuthChannelInterceptor) {
        this.stompAuthChannelInterceptor = stompAuthChannelInterceptor;
    }

    /**
     * EN: Configures simple broker destinations and application / user destination prefixes.
     * KA: აკონფიგურირებს მარტივი ბროკერის დანიშნულებებს და აპლიკაციის / მომხმარებლის დანიშნულების პრეფიქსებს.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // EN: /user is handled by the user-destination resolver — do not register it as a broker prefix | KA: /user მუშავდება user-destination რეზოლვერით — ბროკერის პრეფიქსად არ დაარეგისტრირო
        config.enableSimpleBroker("/queue", "/topic");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    /**
     * EN: Registers the /ws SockJS endpoint with CORS origin patterns matching CorsConfig.
     * KA: არეგისტრირებს /ws SockJS ენდპოინტს CORS წარმოშობის პატერნებით, რომლებიც CorsConfig-ს ემთხვევა.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // EN: Patterns match CorsConfig so LAN / alternate Vite ports work in local dev | KA: პატერნები ემთხვევა CorsConfig-ს, რომ LAN / ალტერნატიული Vite პორტები ლოკალურ დევში იმუშაოს
        java.util.LinkedHashSet<String> patterns = new java.util.LinkedHashSet<>();
        patterns.add("http://localhost:*");
        patterns.add("http://127.0.0.1:*");
        patterns.add("http://192.168.*.*:*");
        patterns.add("http://10.*.*.*:*");
        patterns.add("http://172.*.*.*:*");
        // EN: Allow Vite dev server default alternate port used during HMR | KA: Vite დევ სერვერის ალტერნატიული პორტის დაშვება HMR-ისას
        patterns.add("http://localhost:5174");
        if (allowedOrigins != null && !allowedOrigins.isBlank()) {
            for (String origin : allowedOrigins.split(",")) {
                String trimmed = origin.trim();
                if (!trimmed.isEmpty()) {
                    patterns.add(trimmed);
                }
            }
        }
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(patterns.toArray(String[]::new))
                .withSockJS();
    }

    /**
     * EN: Attaches the JWT STOMP interceptor to the client inbound channel.
     * KA: ამაგრებს JWT STOMP ინტერცეპტორს კლიენტის შემომავალ არხზე.
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // EN: Add JWT authentication interceptor for inbound messages | KA: JWT ავთენტიფიკაციის ინტერცეპტორის დამატება შემომავალი შეტყობინებებისთვის
        registration.interceptors(stompAuthChannelInterceptor);
    }
}
