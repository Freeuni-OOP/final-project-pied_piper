package com.lecturboxd.websocket;

import com.lecturboxd.auth.JwtTokenProvider;
import com.lecturboxd.auth.UserDetailsServiceImpl;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * EN: Intercepts STOMP CONNECT to authenticate via JWT and attach the user Principal (use getAccessor, not wrap).
 * KA: იჭერს STOMP CONNECT-ს JWT ავთენტიფიკაციისთვის და მომხმარებლის Principal-ის მისამაგრებლად (გამოიყენე getAccessor, არა wrap).
 */
@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * EN: Injects JWT provider and user details service for CONNECT authentication.
     * KA: ინჯექციას უკეთებს JWT პროვაიდერს და მომხმარებლის დეტალების სერვისს CONNECT ავთენტიფიკაციისთვის.
     */
    public StompAuthChannelInterceptor(JwtTokenProvider jwtTokenProvider, UserDetailsServiceImpl userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * EN: On CONNECT, validates Bearer JWT from native headers and sets the session user.
     * KA: CONNECT-ზე ამოწმებს Bearer JWT-ს native ჰედერებიდან და აყენებს სესიის მომხმარებელს.
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // EN: Must use getAccessor so setUser persists on the session for convertAndSendToUser | KA: უნდა გამოიყენო getAccessor, რომ setUser სესიაზე შენარჩუნდეს convertAndSendToUser-ისთვის
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    if (jwtTokenProvider.validateToken(token)) {
                        String email = jwtTokenProvider.getEmailFromToken(token);
                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        accessor.setUser(authentication);
                    }
                } catch (Exception ex) {
                    // EN: Token validation failed — connection proceeds without a user principal | KA: ტოკენის ვალიდაცია ჩაიშალა — კავშირი გრძელდება მომხმარებლის პრინციპალის გარეშე
                }
            }
        }

        return message;
    }
}
