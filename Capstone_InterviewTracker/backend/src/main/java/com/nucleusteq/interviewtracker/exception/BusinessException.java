package com.nucleusteq.interviewtracker.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown for business logic errors (e.g., duplicate entries,
 * invalid state transitions, conflicts).
 * Returns HTTP 409 Conflict or 422 Unprocessable Entity.
 */
public class BusinessException extends BaseException {

    private static final String ERROR_CODE = "BUSINESS_ERROR";

    public BusinessException(String message) {
        super(message, HttpStatus.CONFLICT, ERROR_CODE);
    }

    public BusinessException(String message, HttpStatus status) {
        super(message, status, ERROR_CODE);
    }

    /**
     * Creates exception for duplicate resource errors
     */
    public static BusinessException duplicate(String resourceName, String field, String value) {
        return new BusinessException(
            String.format("%s with %s '%s' already exists", resourceName, field, value),
            HttpStatus.CONFLICT
        );
    }

    /**
     * Creates exception for invalid state transitions
     */
    public static BusinessException invalidState(String currentState, String attemptedAction) {
        return new BusinessException(
            String.format("Cannot perform '%s' from current state '%s'", attemptedAction, currentState),
            HttpStatus.UNPROCESSABLE_ENTITY
        );
    }
}
