package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.dto.LoginRequestDto;
import com.nucleusteq.interviewtracker.dto.LoginResponseDto;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import com.nucleusteq.interviewtracker.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Service class that handles the business logic for authentication.
 *
 * The flow for login is:
 * 1. Take the email and password from the request
 * 2. Ask Spring Security's AuthenticationManager to verify them
 * 3. If valid, fetch the full user from DB to get their name and role
 * 4. Generate a JWT token for that user
 * 5. Return the token + user info as a LoginResponseDto
 *
 * We let Spring Security handle the actual credential verification
 * instead of doing it manually — that way password hashing,
 * account status checks etc. are all handled for us automatically.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * Constructor injection — all three dependencies are required
     * for this service to function.
     *
     * @param authenticationManager Spring Security's manager that
     *                              verifies credentials against the database
     * @param userRepository        used to fetch full user details after auth
     * @param jwtUtil               used to generate the JWT token
     */
    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Handles the login process end to end.
     *
     * We catch specific Spring Security exceptions and convert them
     * into meaningful messages. This way the controller stays clean
     * and all the auth logic lives here in the service layer.
     *
     * Possible failure cases:
     * - Wrong password → BadCredentialsException
     * - Account not activated yet → DisabledException
     * - Email doesn't exist → BadCredentialsException (Spring wraps it)
     *
     * @param request the login request containing email and password
     * @return LoginResponseDto with JWT token and user details
     * @throws BadCredentialsException if email or password is wrong
     * @throws DisabledException       if the account has not been activated
     */
    public LoginResponseDto login(LoginRequestDto request) {

        /*
         * This single line does a lot:
         * - Calls UserDetailsServiceImpl.loadUserByUsername(email)
         * - Compares the provided password against the BCrypt hash in DB
         * - Checks if the account is active (isActive flag on User)
         * - Throws an exception if any of these checks fail
         */
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (DisabledException e) {
            /*
             * This happens when isActive = false on the User.
             * Common for panel members who haven't set their password yet.
             */
            throw new DisabledException(
                    "Your account has not been activated yet. "
                    + "Please check your email for the activation link."
            );
        } catch (BadCredentialsException e) {
            // wrong email or wrong password — we give the same message
            // intentionally to not reveal which one is wrong
            throw new BadCredentialsException(
                    "Invalid email or password. Please try again."
            );
        }

        /*
         * Authentication passed — now fetch the full User entity
         * so we can get their name, role etc. for the response.
         * We use the email from the authentication principal
         * instead of the request directly — safer practice.
         */
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "User not found after authentication — this should never happen."
                ));

        /*
         * Generate the JWT token with email and role embedded.
         * Role is stored as a string like "HR", "CANDIDATE", "PANEL"
         * so the frontend can use it directly.
         */
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new LoginResponseDto(
                token,
                user.getRole().name(),
                user.getFullName(),
                user.getEmail()
        );
    }
}