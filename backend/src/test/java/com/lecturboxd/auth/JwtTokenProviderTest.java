package com.lecturboxd.auth;

import com.lecturboxd.config.JwtProperties;
import com.lecturboxd.entity.User;
import com.lecturboxd.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-secret-key-must-be-at-least-32-characters-long");
        properties.setExpirationMs(3_600_000);
        jwtTokenProvider = new JwtTokenProvider(properties);
    }

    @Test
    void generatesAndValidatesToken() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("student@freeuni.edu.ge");
        user.setName("Test User");

        String token = jwtTokenProvider.generateToken(user);

        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("student@freeuni.edu.ge", jwtTokenProvider.getEmailFromToken(token));
        assertEquals(user.getId(), jwtTokenProvider.getUserIdFromToken(token));
    }

    @Test
    void validateTokenReturnsFalseForGarbage() {
        assertFalse(jwtTokenProvider.validateToken("not-a-jwt"));
    }

    @Test
    void getEmailFromTokenThrowsForInvalidToken() {
        assertThrows(UnauthorizedException.class,
                () -> jwtTokenProvider.getEmailFromToken("not-a-jwt"));
    }
}
