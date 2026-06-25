package com.lecturboxd.dto.response;

import java.time.Instant;

public class RegisterResponse {

    private String message;
    private String email;
    private Instant expiresAt;
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
