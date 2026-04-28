package com.nucleusteq.interviewtracker.controller;

import com.nucleusteq.interviewtracker.dto.LoginRequestDto;
import com.nucleusteq.interviewtracker.dto.LoginResponseDto;
import com.nucleusteq.interviewtracker.service.AuthService;
import com.nucleusteq.interviewtracker.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that handles all authentication-related endpoints.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructor injection — keeps this testable and explicit.
     */
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Handles user login requests.
     * Accepts email and password, verifies them, and returns a JWT token
     * along with user details — all wrapped in a standard ApiResponse.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto request) {

        try {
            LoginResponseDto loginResponse = authService.login(request);

            return ResponseEntity.ok(
                    ApiResponse.success("Login successful", loginResponse)
            );

        } catch (DisabledException e) {
           
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));

        } catch (BadCredentialsException e) {
           
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            "Something went wrong. Please try again later."
                    ));
        }
    }

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<LoginResponseDto>> signup(
            @Valid @RequestBody com.nucleusteq.interviewtracker.dto.SignupRequestDto request) {
        try {
            LoginResponseDto response = authService.signup(request, passwordEncoder);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Account created successfully. Please check your email for verification link.", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Something went wrong."));
        }
    }

    @PostMapping("/verify-candidate")
    public ResponseEntity<ApiResponse<Void>> verifyCandidate(@org.springframework.web.bind.annotation.RequestParam String token) {
        try {
            authService.verifyCandidate(token);
            return ResponseEntity.ok(ApiResponse.success("Email verified successfully. You can now login.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Something went wrong."));
        }
    }
}