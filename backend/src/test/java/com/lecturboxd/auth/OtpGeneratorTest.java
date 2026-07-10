package com.lecturboxd.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OtpGeneratorTest {

    @Test
    void generateSixDigitCodeIsAlwaysSixDigits() {
        OtpGenerator generator = new OtpGenerator();
        for (int i = 0; i < 50; i++) {
            String code = generator.generateSixDigitCode();
            assertEquals(6, code.length());
            int value = Integer.parseInt(code);
            assertTrue(value >= 100_000 && value <= 999_999);
        }
    }
}
