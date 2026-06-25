package com.lecturboxd.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;

@Component
public class UniversityEmailValidator {

    private final String[] allowedDomains;

    public UniversityEmailValidator(
            @Value("${lecturboxd.allowed-email-domains}") String allowedDomains
    ) {
        this.allowedDomains = Arrays.stream(allowedDomains.split(","))
                .map(String::trim)
                .map(domain -> domain.toLowerCase(Locale.ROOT))
                .toArray(String[]::new);
    }

    public void validate(String email) {
        if (email == null || email.isBlank()) {
            throw new com.lecturboxd.exception.InvalidUniversityEmailException("Email is required");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        boolean valid = Arrays.stream(allowedDomains)
                .anyMatch(normalizedEmail::endsWith);

        if (!valid) {
            throw new com.lecturboxd.exception.InvalidUniversityEmailException(
                    "Email must end with @freeuni.edu.ge or @agruni.edu.ge"
            );
        }
    }
}
