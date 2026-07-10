package com.lecturboxd.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * EN: Once-per-request filter that authenticates HTTP requests using a Bearer JWT.
 * KA: ერთჯერადი ფილტრი, რომელიც HTTP მოთხოვნებს ავთენტიფიცირებს Bearer JWT-ით.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * EN: Creates the filter with JWT provider and user details service dependencies.
     * KA: ქმნის ფილტრს JWT პროვაიდერისა და მომხმარებლის დეტალების სერვისის დამოკიდებულებებით.
     */
    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            UserDetailsServiceImpl userDetailsService
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * EN: Extracts and validates the Bearer token, then sets SecurityContext authentication when needed.
     * KA: ამოიღებს და ამოწმებს Bearer ტოკენს, შემდეგ საჭიროებისას აყენებს SecurityContext-ის ავთენტიფიკაციას.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // EN: Only process Authorization headers that use the Bearer scheme | KA: მხოლოდ Bearer სქემის Authorization ჰედერების დამუშავება
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Authentication existing = SecurityContextHolder.getContext().getAuthentication();
            // EN: Authenticate only when context is empty or anonymous | KA: ავთენტიფიკაცია მხოლოდ ცარიელი ან ანონიმური კონტექსტისას
            boolean needsAuth = existing == null || existing instanceof AnonymousAuthenticationToken;

            if (needsAuth && jwtTokenProvider.validateToken(token)) {
                try {
                    String email = jwtTokenProvider.getEmailFromToken(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception ex) {
                    // EN: Clear context on failure so a bad token does not leave partial auth | KA: წარუმატებლობისას კონტექსტის გასუფთავება, რომ ცუდი ტოკენი ნაწილობრივ ავთენტიფიკაციას არ დატოვოს
                    log.warn("JWT authentication failed for {}: {}", request.getRequestURI(), ex.getMessage());
                    SecurityContextHolder.clearContext();
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
