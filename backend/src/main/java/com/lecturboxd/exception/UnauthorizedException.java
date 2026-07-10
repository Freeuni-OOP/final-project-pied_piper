package com.lecturboxd.exception;

/**
 * EN: Thrown when authentication is missing or invalid (HTTP 401).
 * KA: იგდებს, როცა ავთენტიფიკაცია არ არსებობს ან არასწორია (HTTP 401).
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
