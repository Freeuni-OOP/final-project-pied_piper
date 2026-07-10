package com.lecturboxd.config;

import com.lecturboxd.auth.AdminApiKeyFilter;
import com.lecturboxd.auth.JwtAuthenticationFilter;
import com.lecturboxd.auth.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * EN: Central Spring Security setup: stateless JWT filter chain, CORS, and public route rules.
 * KA: ცენტრალური Spring Security კონფიგურაცია: უსესიო JWT ფილტრების ჯაჭვი, CORS და საჯარო მარშრუტების წესები.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AdminApiKeyFilter adminApiKeyFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;

    /**
     * EN: Injects auth filters, user details service, and CORS configuration source.
     * KA: ინჯექციას უკეთებს ავთენტიფიკაციის ფილტრებს, მომხმარებლის დეტალების სერვისს და CORS კონფიგურაციის წყაროს.
     */
    public SecurityConfig(
            AdminApiKeyFilter adminApiKeyFilter,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            UserDetailsServiceImpl userDetailsService,
            CorsConfigurationSource corsConfigurationSource
    ) {
        this.adminApiKeyFilter = adminApiKeyFilter;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    /**
     * EN: Builds the HTTP security filter chain with permit-all paths and JWT/admin filters.
     * KA: აგებს HTTP უსაფრთხოების ფილტრების ჯაჭვს permit-all გზებით და JWT/ადმინ ფილტრებით.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // EN: Stateless API — CSRF disabled; sessions are not used | KA: უსესიო API — CSRF გამორთულია; სესიები არ გამოიყენება
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required")
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeJsonError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied")
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        // EN: Public auth, admin (key-checked in filter), WebSocket, and Swagger paths | KA: საჯარო auth, ადმინი (გასაღები ფილტრში), WebSocket და Swagger გზები
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/index.html", "/favicon.ico").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers("/ws/**", "/ws").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .addFilterBefore(adminApiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * EN: Writes a JSON error body for authentication or authorization failures.
     * KA: წერს JSON შეცდომის სხეულს ავთენტიფიკაციის ან ავტორიზაციის წარუმატებლობისას.
     */
    private static void writeJsonError(HttpServletResponse response, int status, String message) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
                String.format(
                        "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                        Instant.now(),
                        status,
                        status == 401 ? "Unauthorized" : "Forbidden",
                        message.replace("\"", "\\\"")
                )
        );
    }

    /**
     * EN: Provides BCrypt password hashing for registration and login.
     * KA: უზრუნველყოფს BCrypt პაროლის ჰეშირებას რეგისტრაციისა და ლოგინისთვის.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * EN: Exposes the AuthenticationManager bean used by auth services.
     * KA: აქვეყნებს AuthenticationManager bean-ს, რომელსაც auth სერვისები იყენებენ.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
