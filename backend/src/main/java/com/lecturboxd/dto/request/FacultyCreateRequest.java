package com.lecturboxd.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * EN: Request body for creating a new faculty.
 * KA: მოთხოვნის სხეული ახალი ფაკულტეტის შესაქმნელად.
 */
public class FacultyCreateRequest {

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
