package com.lecturboxd.exception;

/**
 * EN: Thrown when a request conflicts with the current state of a resource (HTTP 409).
 * KA: იგდებს, როცა მოთხოვნა ეწინააღმდეგება რესურსის მიმდინარე მდგომარეობას (HTTP 409).
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
