package com.lecturboxd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

/**
 * EN: Configures Cross-Origin Resource Sharing (CORS) for the LecturBoxd REST API.
 * KA: აკონფიგურირებს Cross-Origin Resource Sharing (CORS) LecturBoxd REST API-სთვის.
 */
@Configuration
public class CorsConfig {

    @Value("${lecturboxd.cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    /**
     * EN: Builds a CORS source allowing local/LAN Vite origins plus configured extra origins.
     * KA: აგებს CORS წყაროს, რომელიც უშვებს ლოკალურ/LAN Vite წარმოშობებს და კონფიგურირებულ დამატებით წარმოშობებს.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // EN: Patterns cover Vite on any port and LAN IPs (host: true), which exact origins miss | KA: პატერნები ფარავს Vite-ს ნებისმიერ პორტზე და LAN IP-ებს, რასაც ზუსტი origins ვერ ფარავს
        List<String> patterns = new ArrayList<>(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://192.168.*.*:*",
                "http://10.*.*.*:*",
                "http://172.*.*.*:*"
        ));
        if (allowedOrigins != null && !allowedOrigins.isBlank()) {
            for (String origin : allowedOrigins.split(",")) {
                String trimmed = origin.trim();
                if (!trimmed.isEmpty() && !patterns.contains(trimmed)) {
                    patterns.add(trimmed);
                }
            }
        }
        configuration.setAllowedOriginPatterns(patterns);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
