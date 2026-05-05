package com.nucleusteq.interviewtracker.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LoginResponseDtoTest {

    @Test
    void constructorShouldPopulateAllFields() {
        LoginResponseDto response = new LoginResponseDto("jwt-token", "HR", "Asha Verma", "asha@example.com");

        assertEquals("jwt-token", response.getToken());
        assertEquals("HR", response.getRole());
        assertEquals("Asha Verma", response.getFullName());
        assertEquals("asha@example.com", response.getEmail());
    }

    @Test
    void settersShouldUpdateValues() {
        LoginResponseDto response = new LoginResponseDto();

        response.setToken("token-123");
        response.setRole("CANDIDATE");
        response.setFullName("Rahul Sharma");
        response.setEmail("rahul@example.com");

        assertEquals("token-123", response.getToken());
        assertEquals("CANDIDATE", response.getRole());
        assertEquals("Rahul Sharma", response.getFullName());
        assertEquals("rahul@example.com", response.getEmail());
    }

    @Test
    void defaultConstructorShouldLeaveFieldsNull() {
        LoginResponseDto response = new LoginResponseDto();

        assertNull(response.getToken());
        assertNull(response.getRole());
        assertNull(response.getFullName());
        assertNull(response.getEmail());
    }
}