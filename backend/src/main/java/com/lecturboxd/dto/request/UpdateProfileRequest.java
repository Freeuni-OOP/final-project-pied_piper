package com.lecturboxd.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * EN: Request body for updating the authenticated user's display name.
 * KA: მოთხოვნის სხეული ავთენტიფიცირებული მომხმარებლის საჩვენებელი სახელის განსაახლებლად.
 */
public class UpdateProfileRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
