package com.nucleusteq.interviewtracker.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when user is authenticated but not authorized to perform action.
 * Returns HTTP 403 Forbidden.
 */
public class ForbiddenException extends BaseException {

    private static final HttpStatus STATUS = HttpStatus.FORBIDDEN;
    private static final String ERROR_CODE = "FORBIDDEN";

    public ForbiddenException() {
        super("Access denied", STATUS, ERROR_CODE);
    }

    public ForbiddenException(String message) {
        super(message, STATUS, ERROR_CODE);
    }

    public ForbiddenException(String resource, String action) {
        super(String.format("Not authorized to %s this %s", action, resource), STATUS, ERROR_CODE);
    }
}
