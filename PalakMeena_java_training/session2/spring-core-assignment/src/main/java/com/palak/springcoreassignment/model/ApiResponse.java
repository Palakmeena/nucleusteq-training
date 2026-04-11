package com.palak.springcoreassignment.model;

/**
 * ApiResponse is a generic wrapper for all REST API responses.
 * Ensures consistent response format across all endpoints.
 *
 * Demonstrates: Generic types, builder pattern, and consistent API contract.
 */
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * Creates a successful response with data.
     */
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Creates an error response (no data).
     */
    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
