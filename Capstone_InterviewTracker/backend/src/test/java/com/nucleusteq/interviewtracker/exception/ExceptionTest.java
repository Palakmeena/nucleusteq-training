package com.nucleusteq.interviewtracker.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void testBusinessException() {
        String message = "Business error occurred";
        BusinessException ex = new BusinessException(message);
        
        assertEquals(message, ex.getMessage());
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void testResourceNotFoundException() {
        String message = "Resource not found";
        ResourceNotFoundException ex = new ResourceNotFoundException(message);
        
        assertEquals(message, ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void testValidationException() {
        String message = "Validation error";
        ValidationException ex = new ValidationException(message);
        
        assertEquals(message, ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void testUnauthorizedException() {
        String message = "User not authorized";
        UnauthorizedException ex = new UnauthorizedException(message);
        
        assertEquals(message, ex.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }

    @Test
    void testForbiddenException() {
        String message = "Access forbidden";
        ForbiddenException ex = new ForbiddenException(message);
        
        assertEquals(message, ex.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void testErrorResponse() {
        int status = 400;
        String error = "Bad Request";
        String errorCode = "VALIDATION_ERROR";
        String message = "Validation failed";
        String path = "/api/test";
        
        ErrorResponse response = new ErrorResponse(status, error, errorCode, message, path);
        
        assertEquals(status, response.getStatus());
        assertEquals(error, response.getError());
        assertEquals(errorCode, response.getErrorCode());
        assertEquals(message, response.getMessage());
        assertEquals(path, response.getPath());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testErrorResponseSetters() {
        ErrorResponse response = new ErrorResponse();
        
        response.setStatus(404);
        response.setMessage("Not found");
        response.setError("Not Found");
        response.setErrorCode("RESOURCE_NOT_FOUND");
        response.setPath("/api/resource/1");
        LocalDateTime now = LocalDateTime.now();
        response.setTimestamp(now);
        
        assertEquals(404, response.getStatus());
        assertEquals("Not found", response.getMessage());
        assertEquals("Not Found", response.getError());
    }

    @Test
    void testErrorResponseDefaultConstructor() {
        ErrorResponse response = new ErrorResponse();
        
        assertNotNull(response.getTimestamp());
    }
}
