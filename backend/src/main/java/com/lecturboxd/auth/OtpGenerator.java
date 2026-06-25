package com.lecturboxd.auth;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpGenerator {

    private final SecureRandom secureRandom = new SecureRandom();

    public String generateSixDigitCode() {
        int code = secureRandom.nextInt(900_000) + 100_000;
        return String.valueOf(code);
    }
}
