package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.dto.LoginRequestDto;
import com.nucleusteq.interviewtracker.dto.LoginResponseDto;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.mapper.AuthMapper;
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
 * Service class that handles business logic for authentication.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthMapper authMapper;

    /**
     * Constructor injection for required dependencies.
     */
    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtUtil jwtUtil,
                       AuthMapper authMapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authMapper = authMapper;
    }

    /**
     * Handles the login process end to end.
     *
     * @param request the login request DTO
     * @return the login response DTO
     */
    public LoginResponseDto login(LoginRequestDto request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (DisabledException e) {
            throw new DisabledException(
                    "Your account has not been activated yet. "
                    + "Please check your email for the activation link."
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(
                    "Invalid email or password. Please try again."
            );
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "User not found after authentication — this should never happen."
                ));

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return authMapper.mapToLoginResponse(user, token);
    }

    /**
     * Handles candidate registration.
     */
    public LoginResponseDto signup(com.nucleusteq.interviewtracker.dto.SignupRequestDto request, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }

        User user = new User(
                request.getFullName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                com.nucleusteq.interviewtracker.enums.UserRole.CANDIDATE
        );
        user.setActive(true);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return authMapper.mapToLoginResponse(user, token);
    }
}