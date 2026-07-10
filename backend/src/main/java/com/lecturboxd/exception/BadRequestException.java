package com.lecturboxd.exception;

/**
 * EN: Thrown when a client request is syntactically valid but semantically invalid (HTTP 400).
 * KA: იგდებს, როცა კლიენტის მოთხოვნა სინტაქსურად სწორია, მაგრამ სემანტიკურად არასწორი (HTTP 400).
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
