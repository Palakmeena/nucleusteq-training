package com.nucleusteq.interviewtracker.controller;

import com.nucleusteq.interviewtracker.dto.LoginRequestDto;
import com.nucleusteq.interviewtracker.dto.LoginResponseDto;
import com.nucleusteq.interviewtracker.dto.SignupRequestDto;
import com.nucleusteq.interviewtracker.exception.BusinessException;
import com.nucleusteq.interviewtracker.service.AuthService;
import com.nucleusteq.interviewtracker.util.ApiResponse;
import com.nucleusteq.interviewtracker.util.AppConstants;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for authentication endpoints.
 * Handles user login, signup, and activation operations.
 */
@RestController
@RequestMapping(AppConstants.AUTH_BASE)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(AppConstants.LOGIN)
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto request) {

        try {
            // If frontend sent Base64-encoded password, decode it here (backwards-compatible)
            String pw = request.getPassword();
            if (pw != null && pw.matches("^[A-Za-z0-9+/=]+$") ) {
                try {
                    byte[] decoded = Base64.getDecoder().decode(pw);
                    String decodedStr = new String(decoded, StandardCharsets.UTF_8);
                    if (decodedStr.length() >= 6) {
                        request.setPassword(decodedStr);
                    }
                } catch (IllegalArgumentException ignored) {
                    // not valid base64, keep original
                }
            }
            LoginResponseDto loginResponse = authService.login(request);
            logger.info("User logged in: {}", request.getEmail());
            return ResponseEntity.ok(ApiResponse.success(AppConstants.LOGIN_SUCCESS, loginResponse));

        } catch (DisabledException e) {
            logger.warn("Login attempt for disabled account: {} - {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(e.getMessage()));

        } catch (BadCredentialsException e) {
            logger.warn("Bad credentials for user: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            logger.error("Unexpected error during login for {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
        }
    }

    @PostMapping(AppConstants.SIGNUP)
    public ResponseEntity<ApiResponse<LoginResponseDto>> signup(
            @Valid @RequestBody SignupRequestDto request) {
        try {
            LoginResponseDto response = authService.signup(request, passwordEncoder);
            logger.info("New account created: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(AppConstants.ACCOUNT_CREATED, response));
        } catch (BusinessException e) {
            logger.warn("Signup business rule failed for {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(ApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.warn("Signup validation failed for {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during signup for {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
        }
    }

    @PostMapping(AppConstants.VERIFY_CANDIDATE)
    public ResponseEntity<ApiResponse<Void>> verifyCandidate(@org.springframework.web.bind.annotation.RequestParam String token) {
        try {
            authService.verifyCandidate(token);
            logger.info("Candidate verified via token");
            return ResponseEntity.ok(ApiResponse.success(AppConstants.EMAIL_VERIFIED, null));
        } catch (BusinessException e) {
            logger.warn("Candidate verification business rule failed: {}", e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(ApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.warn("Candidate verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during candidate verification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
        }
    }
}