package com.lecturboxd.exception;

/**
 * EN: Thrown when a registration email is not an allowed university domain address (HTTP 400).
 * KA: იგდებს, როცა რეგისტრაციის ელფოსტა არ არის დაშვებული უნივერსიტეტის დომენის მისამართი (HTTP 400).
 */
public class InvalidUniversityEmailException extends RuntimeException {

    public InvalidUniversityEmailException(String message) {
        super(message);
    }
}
