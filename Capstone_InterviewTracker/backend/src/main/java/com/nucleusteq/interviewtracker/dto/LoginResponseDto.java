package com.nucleusteq.interviewtracker.dto;

/**
 * DTO for the login response sent back to the client.
 */
public class LoginResponseDto {

    /**
     * The JWT token the client must include in the Authorization header
     */
    private String token;

    /**
     * The role of the logged-in user — HR, CANDIDATE, or PANEL.
     */
    private String role;

    /**
     * Full name of the logged-in user.
     */
    private String fullName;

    /**
     * Email of the logged-in user.
     */
    private String email;

    /**
     * Default constructor required for JSON serialization.
     */
    public LoginResponseDto() {
    }

    /**
     * All-args constructor — used in AuthService to build the response
     * after a successful login.
     */
    public LoginResponseDto(String token, String role,
                            String fullName, String email) {
        this.token = token;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}