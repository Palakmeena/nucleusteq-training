package com.nucleusteq.interviewtracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT filter that runs once for every incoming HTTP request.
 *
 * This is the gatekeeper of the application. Before any request reaches
 * a controller, this filter checks if there's a valid JWT token in the
 * Authorization header. If yes, it sets the user's authentication in
 * Spring Security's context so the rest of the app knows who is making
 * the request and what role they have.
 *
 * Extends OncePerRequestFilter to guarantee it runs exactly once
 * per request — Spring can sometimes call filters multiple times
 * without this.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Constructor injection keeps this testable and explicit
     * about what this filter depends on.
     *
     * @param jwtUtil            used to extract and validate the token
     * @param userDetailsService used to load the user from database
     */
    @Autowired
    public JwtFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Core filter logic — runs on every request.
     *
     * The flow is:
     * 1. Read the Authorization header
     * 2. If it starts with "Bearer ", extract the token
     * 3. Extract the email from the token
     * 4. Load the user from database using that email
     * 5. Validate the token against that user
     * 6. If valid, set authentication in SecurityContext
     * 7. Either way, pass the request along to the next filter
     *
     * If anything goes wrong (token missing, expired, tampered),
     * we just don't set the authentication — Spring Security will
     * then reject the request with a 401 Unauthorized automatically.
     *
     * @param request     the incoming HTTP request
     * @param response    the outgoing HTTP response
     * @param filterChain the chain of filters to pass the request through
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        /*
         * Authorization header format is: "Bearer <token>"
         * We check for "Bearer " prefix before trying to extract anything.
         * If the header is missing or has a different format, we skip auth
         * and let Spring Security handle it (it will return 401).
         */
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // strip "Bearer " prefix
            try {
                email = jwtUtil.extractEmail(token);
            } catch (Exception e) {
                /*
                 * Token is malformed or signature is invalid.
                 * We log nothing here intentionally — this could just be
                 * an expired token or a bad request, not necessarily an attack.
                 * We let it fall through and Spring Security will return 401.
                 */
                logger.warn("Could not extract email from JWT token: " + e.getMessage());
            }
        }

        /*
         * Only proceed if we got an email from the token AND
         * the SecurityContext doesn't already have an authenticated user.
         * The second check avoids re-authenticating on every filter call
         * if authentication was already set earlier in the chain.
         */
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtUtil.validateToken(token, userDetails.getUsername())) {

                /*
                 * Create an authentication token with the user's details
                 * and their granted authorities (roles).
                 * The credentials are set to null here — we don't need the
                 * password after the JWT is validated.
                 */
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // attach request details like IP address to the auth token
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // set the authentication in the context — user is now authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // always pass the request to the next filter regardless of auth result
        filterChain.doFilter(request, response);
    }
}