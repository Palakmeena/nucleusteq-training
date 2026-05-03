package com.nucleusteq.interviewtracker.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when request validation fails.
 * Returns HTTP 400 Bad Request.
 */
public class ValidationException extends BaseException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    private static final String ERROR_CODE = "VALIDATION_ERROR";

    public ValidationException(String field, String reason) {
        super(String.format("Validation failed for field '%s': %s", field, reason), STATUS, ERROR_CODE);
    }

    public ValidationException(String message) {
        super(message, STATUS, ERROR_CODE);
    }
}
