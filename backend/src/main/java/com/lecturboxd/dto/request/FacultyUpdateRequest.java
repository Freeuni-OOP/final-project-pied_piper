package com.lecturboxd.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * EN: Request body for updating an existing faculty's name.
 * KA: მოთხოვნის სხეული არსებული ფაკულტეტის სახელის განსაახლებლად.
 */
public class FacultyUpdateRequest {

    @NotBlank(message = "Faculty name is required")
    @Size(max = 255, message = "Faculty name must be at most 255 characters")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
