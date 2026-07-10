package com.lecturboxd.exception;

/**
 * EN: Thrown when a requested entity (user, lecture, review, etc.) cannot be found (HTTP 404).
 * KA: იგდებს, როცა მოთხოვნილი ერთეული (მომხმარებელი, ლექცია, მიმოხილვა და სხვ.) ვერ მოიძებნა (HTTP 404).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
