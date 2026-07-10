package com.lecturboxd.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;

/**
 * EN: Ensures registration emails belong to configured university domains.
 * KA: უზრუნველყოფს, რომ რეგისტრაციის ელფოსტები ეკუთვნოდეს კონფიგურირებულ საუნივერსიტეტო დომენებს.
 */
@Component
public class UniversityEmailValidator {

    private final String[] allowedDomains;

    /**
     * EN: Parses the comma-separated allowed-domain list from application configuration.
     * KA: პარსავს მძიმით გამოყოფილ დაშვებული დომენების სიას აპლიკაციის კონფიგურაციიდან.
     */
    public UniversityEmailValidator(
            @Value("${lecturboxd.allowed-email-domains}") String allowedDomains
    ) {
        this.allowedDomains = Arrays.stream(allowedDomains.split(","))
                .map(String::trim)
                .map(domain -> domain.toLowerCase(Locale.ROOT))
                .toArray(String[]::new);
    }

    /**
     * EN: Throws InvalidUniversityEmailException when the email is blank or not on an allowed domain.
     * KA: ისვრის InvalidUniversityEmailException-ს, როცა ელფოსტა ცარიელია ან დაშვებულ დომენზე არ არის.
     */
    public void validate(String email) {
        if (email == null || email.isBlank()) {
            throw new com.lecturboxd.exception.InvalidUniversityEmailException("Email is required");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        // EN: Accept if the normalized address ends with any configured domain suffix | KA: მიღება, თუ ნორმალიზებული მისამართი მთავრდება რომელიმე კონფიგურირებული დომენის სუფიქსით
        boolean valid = Arrays.stream(allowedDomains)
                .anyMatch(normalizedEmail::endsWith);

        if (!valid) {
            throw new com.lecturboxd.exception.InvalidUniversityEmailException(
                    "Email must end with @freeuni.edu.ge or @agruni.edu.ge"
            );
        }
    }
}
