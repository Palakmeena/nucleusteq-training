package com.nucleusteq.interviewtracker.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found.
 * Returns HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends BaseException {

    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;
    private static final String ERROR_CODE = "RESOURCE_NOT_FOUND";

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s not found with id: %d", resourceName, id), STATUS, ERROR_CODE);
    }

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(String.format("%s not found with identifier: %s", resourceName, identifier), STATUS, ERROR_CODE);
    }

    public ResourceNotFoundException(String message) {
        super(message, STATUS, ERROR_CODE);
    }
}
