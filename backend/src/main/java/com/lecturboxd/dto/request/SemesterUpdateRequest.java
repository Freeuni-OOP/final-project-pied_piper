package com.lecturboxd.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SemesterUpdateRequest {

    @NotBlank(message = "Semester number is required")
    @Size(max = 20, message = "Semester number must be at most 20 characters")
    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
