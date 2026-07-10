package com.lecturboxd.auth;

import com.lecturboxd.config.AdminProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminApiKeyFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private FilterChain filterChain;

    @Test
    void shouldNotFilterNonAdminPaths() {
        AdminApiKeyFilter filter = new AdminApiKeyFilter(new AdminProperties());
        when(request.getRequestURI()).thenReturn("/api/users/me");
        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldFilterAdminPaths() {
        AdminApiKeyFilter filter = new AdminApiKeyFilter(new AdminProperties());
        when(request.getRequestURI()).thenReturn("/api/admin/faculties");
        assertFalse(filter.shouldNotFilter(request));
    }

    @Test
    void rejectsWhenApiKeyNotConfigured() throws Exception {
        AdminProperties props = new AdminProperties();
        props.setApiKey("  ");
        AdminApiKeyFilter filter = new AdminApiKeyFilter(props);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("not configured"));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void rejectsMissingOrInvalidKey() throws Exception {
        AdminProperties props = new AdminProperties();
        props.setApiKey("secret");
        AdminApiKeyFilter filter = new AdminApiKeyFilter(props);
        when(request.getHeader("X-Admin-Api-Key")).thenReturn(null);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid or missing"));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void rejectsWrongKey() throws Exception {
        AdminProperties props = new AdminProperties();
        props.setApiKey("secret");
        AdminApiKeyFilter filter = new AdminApiKeyFilter(props);
        when(request.getHeader("X-Admin-Api-Key")).thenReturn("wrong");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void allowsMatchingKey() throws Exception {
        AdminProperties props = new AdminProperties();
        props.setApiKey("secret");
        props.setApiKeyHeader("X-Custom-Admin");
        AdminApiKeyFilter filter = new AdminApiKeyFilter(props);
        when(request.getHeader("X-Custom-Admin")).thenReturn("secret");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    void escapesQuotesInUnauthorizedMessage() throws Exception {
        AdminProperties props = new AdminProperties();
        props.setApiKey("");
        AdminApiKeyFilter filter = new AdminApiKeyFilter(props);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertTrue(response.getContentAsString().contains("Admin API key is not configured"));
    }
}
