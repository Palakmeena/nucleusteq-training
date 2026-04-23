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
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Constructor injection keeps this testable and explicit
     * about what this filter depends on.
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
         */
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // strip "Bearer " prefix
            try {
                email = jwtUtil.extractEmail(token);
            } catch (Exception e) {

                logger.warn("Could not extract email from JWT token: " + e.getMessage());
            }
        }

        /*
         * Only proceed if we got an email from the token AND
         * the SecurityContext doesn't already have an authenticated user.
         */
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtUtil.validateToken(token, userDetails.getUsername())) {

                /*
                 * Create an authentication token with the user's details
                 * and their granted authorities (roles).
                 */
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                /* 
                 * attach request details like IP address to the auth token
                 */
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                /*
                 * set the authentication in the context — user is now authenticated
                 */
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        /* 
         * always pass the request to the next filter regardless of auth result 
         */
        filterChain.doFilter(request, response);
    }
}