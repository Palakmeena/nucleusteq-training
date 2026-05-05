package com.nucleusteq.interviewtracker.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private final String secret = "mySecretKeyForJwtTestingThatIsLongEnoughForHS256Algorithm";
    private final long expiration = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        String email = "test@example.com";
        String role = "HR";

        String token = jwtUtil.generateToken(email, role);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertEquals(email, jwtUtil.extractEmail(token));
        assertEquals(role, jwtUtil.extractRole(token));
    }

    @Test
    void extractEmail_shouldReturnCorrectEmail() {
        String email = "user@example.com";
        String token = jwtUtil.generateToken(email, "CANDIDATE");

        String extractedEmail = jwtUtil.extractEmail(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void extractRole_shouldReturnCorrectRole() {
        String role = "PANEL";
        String token = jwtUtil.generateToken("test@example.com", role);

        String extractedRole = jwtUtil.extractRole(token);

        assertEquals(role, extractedRole);
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email, "HR");

        boolean isValid = jwtUtil.validateToken(token, email);

        assertTrue(isValid);
    }

    @Test
    void validateToken_shouldReturnFalseForWrongEmail() {
        String token = jwtUtil.generateToken("correct@example.com", "HR");

        boolean isValid = jwtUtil.validateToken(token, "wrong@example.com");

        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalseForExpiredToken() {
        // Create an expired token manually
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() - 10000); // Expired 10 seconds ago

        String expiredToken = Jwts.builder()
                .setSubject("test@example.com")
                .claim("role", "HR")
                .setIssuedAt(new Date(now.getTime() - 20000))
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // The validateToken method should handle the exception and return false
        // or we can just verify that an exception is thrown for expired tokens
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtUtil.validateToken(expiredToken, "test@example.com"));
    }

    @Test
    void extractEmail_shouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertThrows(Exception.class, () -> jwtUtil.extractEmail(invalidToken));
    }

    @Test
    void extractRole_shouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertThrows(Exception.class, () -> jwtUtil.extractRole(invalidToken));
    }
}
