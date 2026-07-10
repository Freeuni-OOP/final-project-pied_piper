package com.lecturboxd.auth;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * EN: Generates cryptographically strong six-digit one-time passwords (OTP).
 * KA: ქმნის კრიპტოგრაფიულად ძლიერ ექვსნიშნა ერთჯერად პაროლებს (OTP).
 */
@Component
public class OtpGenerator {

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * EN: Returns a random integer code in the inclusive range 100000–999999 as a string.
     * KA: აბრუნებს შემთხვევით მთელ კოდს დიაპაზონში 100000–999999 სტრიქონის სახით.
     */
    public String generateSixDigitCode() {
        // EN: nextInt(900000)+100000 yields a uniform six-digit value | KA: nextInt(900000)+100000 იძლევა ერთგვაროვან ექვსნიშნა მნიშვნელობას
        int code = secureRandom.nextInt(900_000) + 100_000;
        return String.valueOf(code);
    }
}
