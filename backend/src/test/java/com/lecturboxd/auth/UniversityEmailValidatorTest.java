package com.lecturboxd.auth;

import com.lecturboxd.exception.InvalidUniversityEmailException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UniversityEmailValidatorTest {

    private UniversityEmailValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UniversityEmailValidator("@freeuni.edu.ge,@agruni.edu.ge");
    }

    @Test
    void acceptsFreeUniEmail() {
        assertDoesNotThrow(() -> validator.validate("student@freeuni.edu.ge"));
    }

    @Test
    void acceptsAgruniEmail() {
        assertDoesNotThrow(() -> validator.validate("student@agruni.edu.ge"));
    }

    @Test
    void rejectsOtherDomains() {
        assertThrows(InvalidUniversityEmailException.class, () -> validator.validate("student@gmail.com"));
    }
}
