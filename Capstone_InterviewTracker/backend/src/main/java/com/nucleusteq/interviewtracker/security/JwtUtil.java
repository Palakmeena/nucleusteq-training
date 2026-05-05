package com.nucleusteq.interviewtracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


/**
 * Utility class for JWT token generation and validation.
 * Handles token creation, expiry checks, and user detail extraction.
 */
@Component
public class JwtUtil {

    /**
     * Secret key read from application.properties.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Token expiry time in milliseconds, read from application.properties.
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Builds a signing key from the secret string.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a JWT token for a successfully authenticated user.
     */
    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)         // custom claim — stores role inside token
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Parses the token and returns all its claims (the payload data).
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts the email (subject) from the token.
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extracts the role from the token's custom claims.
     * This avoids a database call just to check what role the user has.
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Checks whether the token has passed its expiry date.
     */
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * Validates the token by checking two things:
     * 1. The email in the token matches the user we loaded from the database
     * 2. The token is not expired
     */
    public boolean validateToken(String token, String email) {
        String tokenEmail = extractEmail(token);
        return tokenEmail.equals(email) && !isTokenExpired(token);
    }
}