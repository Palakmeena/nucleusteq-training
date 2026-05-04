package com.nucleusteq.interviewtracker.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void handleBaseException_BusinessException() {
        BusinessException ex = new BusinessException("Business error");
        
        ResponseEntity<ErrorResponse> response = handler.handleBaseException(ex, request);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Business error", response.getBody().getMessage());
    }

    @Test
    void handleBaseException_ResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
        
        ResponseEntity<ErrorResponse> response = handler.handleBaseException(ex, request);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleEntityNotFound() {
        EntityNotFoundException ex = new EntityNotFoundException("Entity not found");
        
        ResponseEntity<ErrorResponse> response = handler.handleEntityNotFound(ex, request);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex, request);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleIllegalState() {
        IllegalStateException ex = new IllegalStateException("Invalid state");
        
        ResponseEntity<ErrorResponse> response = handler.handleIllegalState(ex, request);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void handleGenericException() {
        Exception ex = new Exception("Generic error");
        
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, request);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
