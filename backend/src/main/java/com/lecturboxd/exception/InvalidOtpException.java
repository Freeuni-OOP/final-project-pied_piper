package com.lecturboxd.exception;

/**
 * EN: Thrown when an OTP/verification code is missing, expired, or does not match (HTTP 400).
 * KA: იგდებს, როცა OTP/ვერიფიკაციის კოდი არ არსებობს, ვადაგასულია ან არ ემთხვევა (HTTP 400).
 */
public class InvalidOtpException extends RuntimeException {

    public InvalidOtpException(String message) {
        super(message);
    }
}
