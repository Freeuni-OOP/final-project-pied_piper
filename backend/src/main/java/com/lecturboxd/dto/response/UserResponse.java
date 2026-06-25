package com.lecturboxd.dto.response;

import java.util.UUID;

public class UserResponse {

    private UUID id;
    private String name;
    private String email;
    private boolean verified;

    public UserResponse() {
    }

    public UserResponse(UUID id, String name, String email, boolean verified) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.verified = verified;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
