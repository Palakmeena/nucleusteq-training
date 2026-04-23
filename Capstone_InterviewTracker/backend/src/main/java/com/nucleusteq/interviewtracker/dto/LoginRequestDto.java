package com.nucleusteq.interviewtracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO (Data Transfer Object) for the login request body.
 *
 * This is what the client sends in the request body when logging in.
 * We use a DTO here instead of the User entity directly because:
 * - We only need email and password for login, not the full User object
 * - It keeps the API contract separate from the database model
 * - Validation annotations here only apply to incoming requests,
 *   not to the entity itself
 */
public class LoginRequestDto {

    /**
     * The user's email address used as their login username.
     * Must be a valid email format and cannot be blank.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    /**
     * The user's password in plain text.
     * Will be matched against the BCrypt hash stored in the database.
     * Never logged or stored after authentication is complete.
     */
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Default constructor required for JSON deserialization.
     * Jackson needs this to create the object before setting fields.
     */
    public LoginRequestDto() {
    }

    /**
     * All-args constructor for convenience in tests.
     *
     * @param email    the login email
     * @param password the login password
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