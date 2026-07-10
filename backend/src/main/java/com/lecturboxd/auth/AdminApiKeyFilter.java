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

/**
 * EN: Servlet filter that protects /api/admin/** endpoints with a shared admin API key header.
 * KA: სერვლეტ-ფილტრი, რომელიც იცავს /api/admin/** ენდპოინტებს ადმინის API გასაღების ჰედერით.
 */
@Component
public class AdminApiKeyFilter extends OncePerRequestFilter {

    private final AdminProperties adminProperties;

    /**
     * EN: Creates the filter with admin API key configuration.
     * KA: ქმნის ფილტრს ადმინის API გასაღების კონფიგურაციით.
     */
    public AdminApiKeyFilter(AdminProperties adminProperties) {
        this.adminProperties = adminProperties;
    }

    /**
     * EN: Skips filtering for any path that is not under /api/admin/.
     * KA: გამოტოვებს ფილტრაციას ყველა გზისთვის, რომელიც არ არის /api/admin/ ქვეშ.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/admin/");
    }

    /**
     * EN: Validates the admin API key header before allowing the request through the filter chain.
     * KA: ამოწმებს ადმინის API გასაღების ჰედერს, სანამ მოთხოვნას ფილტრების ჯაჭვში გაუშვებს.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // EN: Reject if no admin key is configured in application properties | KA: უარყოფა, თუ ადმინის გასაღები კონფიგურაციაში არ არის
        if (!adminProperties.isConfigured()) {
            writeUnauthorized(response, "Admin API key is not configured");
            return;
        }

        // EN: Compare provided header value with the configured secret | KA: შედარება მოწოდებული ჰედერის მნიშვნელობისა კონფიგურირებულ საიდუმლოსთან
        String providedKey = request.getHeader(adminProperties.getApiKeyHeader());
        if (providedKey == null || !providedKey.equals(adminProperties.getApiKey())) {
            writeUnauthorized(response, "Invalid or missing admin API key");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * EN: Writes a JSON 401 Unauthorized response body.
     * KA: წერს JSON 401 Unauthorized პასუხის სხეულს.
     */
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
