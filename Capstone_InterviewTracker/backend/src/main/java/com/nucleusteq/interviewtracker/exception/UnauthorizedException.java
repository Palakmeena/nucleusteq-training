package com.nucleusteq.interviewtracker.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when user is not authenticated or authentication fails.
 * Returns HTTP 401 Unauthorized.
 */
public class UnauthorizedException extends BaseException {

    private static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;
    private static final String ERROR_CODE = "UNAUTHORIZED";

    public UnauthorizedException() {
        super("Authentication required", STATUS, ERROR_CODE);
    }

    public UnauthorizedException(String message) {
        super(message, STATUS, ERROR_CODE);
    }

    public UnauthorizedException(String resource, String action) {
        super(String.format("Unauthorized to %s %s", action, resource), STATUS, ERROR_CODE);
    }
}
