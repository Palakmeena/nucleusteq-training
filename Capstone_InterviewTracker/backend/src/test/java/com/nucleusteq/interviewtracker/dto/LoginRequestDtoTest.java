package com.nucleusteq.interviewtracker.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LoginRequestDtoTest {

    @Test
    void defaultConstructorLeavesFieldsNull() {
        LoginRequestDto dto = new LoginRequestDto();
        assertNull(dto.getEmail());
        assertNull(dto.getPassword());
    }

    @Test
    void settersAndGettersWork() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("user@example.com");
        dto.setPassword("secret");

        assertEquals("user@example.com", dto.getEmail());
        assertEquals("secret", dto.getPassword());
    }
}
