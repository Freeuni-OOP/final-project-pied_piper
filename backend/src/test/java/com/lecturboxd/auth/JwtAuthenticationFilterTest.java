package com.lecturboxd.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void continuesWithoutBearerHeader() throws Exception {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtTokenProvider, never()).validateToken(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void continuesWithNonBearerHeader() throws Exception {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic abc");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtTokenProvider, never()).validateToken(anyString());
    }

    @Test
    void skipsWhenAlreadyAuthenticated() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user", "pass", AuthorityUtils.createAuthorityList("ROLE_USER"))
        );
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token");

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenProvider, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void skipsWhenTokenInvalid() throws Exception {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer bad");
        when(jwtTokenProvider.validateToken("bad")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void authenticatesValidTokenOverAnonymous() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new AnonymousAuthenticationToken("key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"))
        );
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer good");
        when(jwtTokenProvider.validateToken("good")).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken("good")).thenReturn("a@freeuni.edu.ge");

        com.lecturboxd.entity.User user = new com.lecturboxd.entity.User();
        user.setId(UUID.randomUUID());
        user.setEmail("a@freeuni.edu.ge");
        user.setPassword("hash");
        user.setVerified(true);
        LecturboxdUserPrincipal principal = new LecturboxdUserPrincipal(user);
        when(userDetailsService.loadUserByUsername("a@freeuni.edu.ge")).thenReturn(principal);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEqualsPrincipal(principal);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void clearsContextWhenLoadUserFails() throws Exception {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer good");
        when(jwtTokenProvider.validateToken("good")).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken("good")).thenReturn("a@freeuni.edu.ge");
        when(userDetailsService.loadUserByUsername("a@freeuni.edu.ge"))
                .thenThrow(new RuntimeException("boom"));
        when(request.getRequestURI()).thenReturn("/api/users/me");

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    private void assertEqualsPrincipal(LecturboxdUserPrincipal principal) {
        Object authPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        org.junit.jupiter.api.Assertions.assertEquals(principal.getUsername(),
                ((LecturboxdUserPrincipal) authPrincipal).getUsername());
    }
}
