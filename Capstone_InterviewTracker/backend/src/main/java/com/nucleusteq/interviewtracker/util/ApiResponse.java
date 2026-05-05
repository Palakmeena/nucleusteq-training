package com.nucleusteq.interviewtracker.util;

/**
 * Generic wrapper for all API responses in the application.
 *
 * Instead of some endpoints returning plain strings and others
 * returning objects, every response goes through this wrapper.
 * This gives the frontend a consistent structure to always expect:
 * {
 *   "success": true/false,
 *   "message": "some message",
 *   "data": { ... } or null
 * }
 *
 * The generic type T lets us wrap any kind of data —
 * a LoginResponseDto, a list of candidates, a single job description etc.
 */
public class ApiResponse<T> {

    /**
     * Whether the request was processed successfully.
     * Frontend can check this first before reading data.
     */
    private boolean success;

    /**
     * Human readable message about what happened.
     * On success: "Login successful", "Candidate created" etc.
     * On failure: the actual error message.
     */
    private String message;

    /**
     * The actual response payload — could be any type.
     * Will be null on failure responses.
     */
    private T data;

    /**
     * Default constructor needed for JSON serialization.
     */
    public ApiResponse() {
    }

    /**
     * Constructor for responses that carry data — typically success cases.
     *
     * @param success true if the operation succeeded
     * @param message description of what happened
     * @param data    the actual response payload
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * Convenience constructor for error responses where there's no data.
     *
     * @param success false for error responses
     * @param message the error message to show
     */
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }

    /**
     * Static factory method for clean success responses with data.
     * Usage: ApiResponse.success("Login successful", responseDto)
     *
     * @param message success message
     * @param data    the payload to return
     * @return a success ApiResponse wrapping the data
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Static factory method for error responses.
     * Usage: ApiResponse.error("Invalid credentials")
     *
     * @param message the error message
     * @return a failure ApiResponse with no data
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}