package com.lecturboxd.websocket;

import com.lecturboxd.auth.JwtTokenProvider;
import com.lecturboxd.auth.LecturboxdUserPrincipal;
import com.lecturboxd.auth.UserDetailsServiceImpl;
import com.lecturboxd.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StompAuthChannelInterceptorTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private StompAuthChannelInterceptor interceptor;

    @Test
    void returnsUnchangedWhenAccessorMissing() {
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).build();

        Message<?> result = interceptor.preSend(message, null);

        assertSame(message, result);
        verify(jwtTokenProvider, never()).validateToken(anyString());
    }

    @Test
    void ignoresNonConnectCommands() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        accessor.setLeaveMutable(true);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        Message<?> result = interceptor.preSend(message, null);

        assertSame(message, result);
        verify(jwtTokenProvider, never()).validateToken(anyString());
    }

    @Test
    void connectWithoutBearerLeavesUserUnset() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setLeaveMutable(true);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        interceptor.preSend(message, null);

        StompHeaderAccessor resultAccessor = StompHeaderAccessor.wrap(message);
        assertNull(resultAccessor.getUser());
    }

    @Test
    void connectWithValidTokenSetsUser() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("Authorization", "Bearer good");
        accessor.setLeaveMutable(true);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        when(jwtTokenProvider.validateToken("good")).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken("good")).thenReturn("a@freeuni.edu.ge");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("a@freeuni.edu.ge");
        user.setPassword("hash");
        user.setVerified(true);
        LecturboxdUserPrincipal principal = new LecturboxdUserPrincipal(user);
        when(userDetailsService.loadUserByUsername("a@freeuni.edu.ge")).thenReturn(principal);

        interceptor.preSend(message, null);

        StompHeaderAccessor resultAccessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        assertNotNull(resultAccessor);
        assertNotNull(resultAccessor.getUser());
        assertEqualsEmail(principal.getUsername(), resultAccessor.getUser().getName());
    }

    @Test
    void connectWithInvalidTokenLeavesUserUnset() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("Authorization", "Bearer bad");
        accessor.setLeaveMutable(true);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        when(jwtTokenProvider.validateToken("bad")).thenReturn(false);

        interceptor.preSend(message, null);

        StompHeaderAccessor resultAccessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        assertNull(resultAccessor.getUser());
    }

    @Test
    void connectSwallowsExceptions() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("Authorization", "Bearer good");
        accessor.setLeaveMutable(true);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        when(jwtTokenProvider.validateToken("good")).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken("good")).thenThrow(new RuntimeException("bad token"));

        interceptor.preSend(message, null);

        StompHeaderAccessor resultAccessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        assertNull(resultAccessor.getUser());
    }

    private static void assertEqualsEmail(String expected, String actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }
}
