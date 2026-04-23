package com.nucleusteq.interviewtracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO (Data Transfer Object) for the login request body.
 */
public class LoginRequestDto {

    /**
     * The user's email address used as their login username.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    /**
     * The user's password in plain text.
     * Will be matched against the BCrypt hash stored in the database.
     */
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Default constructor required for JSON deserialization.
\     */
    public LoginRequestDto() {
    }

    /**
     * All-args constructor for convenience in tests.
     */
    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}