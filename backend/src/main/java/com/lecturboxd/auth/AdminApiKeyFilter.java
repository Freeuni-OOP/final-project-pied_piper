package com.lecturboxd.auth;

import com.lecturboxd.config.AdminProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class AdminApiKeyFilter extends OncePerRequestFilter {

    private final AdminProperties adminProperties;

    public AdminApiKeyFilter(AdminProperties adminProperties) {
        this.adminProperties = adminProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/admin/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!adminProperties.isConfigured()) {
            writeUnauthorized(response, "Admin API key is not configured");
            return;
        }

        String providedKey = request.getHeader(adminProperties.getApiKeyHeader());
        if (providedKey == null || !providedKey.equals(adminProperties.getApiKey())) {
            writeUnauthorized(response, "Invalid or missing admin API key");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String body = String.format(
                "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\"}",
                java.time.Instant.now(),
                message.replace("\"", "\\\"")
        );
        response.getWriter().write(body);
    }
}
