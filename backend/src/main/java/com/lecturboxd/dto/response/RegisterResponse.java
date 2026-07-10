package com.lecturboxd.dto.response;

import java.time.Instant;

/**
 * EN: Response after registration: confirmation message, email, OTP expiry, and optional dev OTP.
 * KA: პასუხი რეგისტრაციის შემდეგ: დადასტურების შეტყობინება, ელფოსტა, OTP ვადა და სურვილისამებრ დევ OTP.
 */
public class RegisterResponse {

    private String message;
    private String email;
    private Instant expiresAt;
    /** EN: OTP returned only in non-production/dev mode for easier testing. KA: OTP, რომელიც მხოლოდ არა-პროდაქშენ/დევ რეჟიმში ბრუნდება ტესტირების გასაადვილებლად. */
    private String devCode;

    public RegisterResponse() {
    }

    public RegisterResponse(String message, String email, Instant expiresAt) {
        this.message = message;
        this.email = email;
        this.expiresAt = expiresAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getDevCode() {
        return devCode;
    }

    public void setDevCode(String devCode) {
        this.devCode = devCode;
    }
}
