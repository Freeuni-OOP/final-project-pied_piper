package com.lecturboxd.exception;

/**
 * EN: Thrown when registering with an email that already belongs to an existing user (HTTP 409).
 * KA: იგდებს, როცა რეგისტრაცია ხდება ელფოსტით, რომელიც უკვე არსებულ მომხმარებელს ეკუთვნის (HTTP 409).
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
