package com.lecturboxd.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * EN: Request body for deleting a user by email (typically a development/admin helper).
 * KA: მოთხოვნის სხეული მომხმარებლის ელფოსტით წასაშლელად (ჩვეულებრივ დეველოპმენტის/ადმინის დამხმარე).
 */
public class DeleteUserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
