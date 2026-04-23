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
 * Utility class for everything JWT related.
 *
 * Handles three things:
 * 1. Generating a token when a user logs in successfully
 * 2. Extracting information (email, role) from an incoming token
 * 3. Validating that a token is legitimate and not expired
 *
 * The token is signed with a secret key so nobody can tamper with it.
 * If someone modifies the token payload, the signature check will fail.
 */
@Component
public class JwtUtil {

    /**
     * Secret key read from application.properties.
     * Used to sign and verify JWT tokens.
     * Should be long and random — never hardcode this in production.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Token expiry time in milliseconds, read from application.properties.
     * Default in our config is 86400000 ms = 24 hours.
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Builds a signing key from the secret string.
     * HMAC-SHA256 is used for signing — it's fast and secure enough
     * for this use case.
     *
     * @return the Key object used to sign/verify tokens
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a JWT token for a successfully authenticated user.
     * We store the email as the subject and the role as a custom claim
     * so we can extract both later without hitting the database again.
     *
     * @param email the user's email — stored as the token subject
     * @param role  the user's role (HR, CANDIDATE, PANEL)
     * @return signed JWT token string
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
     * This is a private helper used by the other methods below.
     * If the token is invalid or expired, this throws a JwtException
     * which we catch in the filter.
     *
     * @param token the JWT token string
     * @return Claims object containing all data stored in the token
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
     * We use this in the filter to identify which user is making the request.
     *
     * @param token the JWT token string
     * @return the email address stored in the token
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extracts the role from the token's custom claims.
     * This avoids a database call just to check what role the user has.
     *
     * @param token the JWT token string
     * @return the role string e.g. "HR", "CANDIDATE", "PANEL"
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Checks whether the token has passed its expiry date.
     *
     * @param token the JWT token string
     * @return true if the token is expired, false if still valid
     */
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * Validates the token by checking two things:
     * 1. The email in the token matches the user we loaded from the database
     * 2. The token is not expired
     *
     * Both conditions must be true for the token to be considered valid.
     *
     * @param token the JWT token string
     * @param email the email of the user loaded from the database
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token, String email) {
        String tokenEmail = extractEmail(token);
        return tokenEmail.equals(email) && !isTokenExpired(token);
    }
}