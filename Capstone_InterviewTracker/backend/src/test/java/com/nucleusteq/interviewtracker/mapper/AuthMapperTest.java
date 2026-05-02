package com.nucleusteq.interviewtracker.mapper;

import com.nucleusteq.interviewtracker.dto.LoginResponseDto;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for AuthMapper.
 * Verifies that User entity is correctly mapped to LoginResponseDto.
 */
class AuthMapperTest {

    private AuthMapper authMapper;

    @BeforeEach
    void setUp() {
        authMapper = new AuthMapper();
    }

    @Test
    void mapToLoginResponse_shouldReturnCorrectDto() {
        User user = new User("Palak Meena", "palak@example.com", "encoded123", UserRole.HR);
        String token = "sample.jwt.token";

        LoginResponseDto result = authMapper.mapToLoginResponse(user, token);

        assertNotNull(result);
        assertEquals(token, result.getToken());
        assertEquals("HR", result.getRole());
        assertEquals("Palak Meena", result.getFullName());
        assertEquals("palak@example.com", result.getEmail());
    }

    @Test
    void mapToLoginResponse_shouldMapCandidateRoleCorrectly() {
        User user = new User("Sarah Gupta", "sarah@example.com", "pass", UserRole.CANDIDATE);

        LoginResponseDto result = authMapper.mapToLoginResponse(user, "token123");

        assertEquals("CANDIDATE", result.getRole());
        assertEquals("Sarah Gupta", result.getFullName());
    }

    @Test
    void mapToLoginResponse_shouldMapPanelRoleCorrectly() {
        User user = new User("John Rao", "john@example.com", "pass", UserRole.PANEL);

        LoginResponseDto result = authMapper.mapToLoginResponse(user, "token456");

        assertEquals("PANEL", result.getRole());
        assertEquals("john@example.com", result.getEmail());
    }
}
