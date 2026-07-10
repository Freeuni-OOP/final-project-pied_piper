package com.lecturboxd.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * EN: Request body for verifying a registration email with a 6-digit OTP code.
 * KA: მოთხოვნის სხეული რეგისტრაციის ელფოსტის 6-ნიშნა OTP კოდით დასადასტურებლად.
 */
public class VerifyOtpRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "\\d{6}", message = "Verification code must be a 6-digit number")
    private String code;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
