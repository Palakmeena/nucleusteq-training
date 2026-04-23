package com.nucleusteq.interviewtracker.dto;

/**
 * DTO for the login response sent back to the client.
 *
 * After a successful login, the client receives:
 * - The JWT token to use in future requests
 * - Their role so the frontend knows which dashboard to show
 * - Their name so the UI can display a greeting
 *
 * We keep this response minimal — only what the frontend actually needs.
 * No sensitive data like password or internal IDs go here.
 */
public class LoginResponseDto {

    /**
     * The JWT token the client must include in the Authorization header
     * for all subsequent requests. Format: "Bearer <token>"
     */
    private String token;

    /**
     * The role of the logged-in user — HR, CANDIDATE, or PANEL.
     * Frontend uses this to decide which dashboard or view to load.
     */
    private String role;

    /**
     * Full name of the logged-in user.
     * Useful for displaying a welcome message on the frontend.
     */
    private String fullName;

    /**
     * Email of the logged-in user.
     * Useful for the frontend to display or store for reference.
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
     *
     * @param token    the generated JWT token
     * @param role     the user's role
     * @param fullName the user's full name
     * @param email    the user's email
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