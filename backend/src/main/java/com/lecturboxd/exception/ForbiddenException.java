package com.lecturboxd.exception;

/**
 * EN: Thrown when an authenticated user is not allowed to perform the requested action (HTTP 403).
 * KA: იგდებს, როცა ავთენტიფიცირებულ მომხმარებელს არ აქვს მოთხოვნილი მოქმედების უფლება (HTTP 403).
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
