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
 *
 * Currently exposes one endpoint:
 * - POST /auth/login → validates credentials and returns a JWT token
 *
 * All responses are wrapped in ApiResponse so the frontend always
 * gets a consistent structure with success flag, message, and data.
 * This controller stays thin — logic lives in AuthService.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructor injection — keeps this testable and explicit.
     *
     * @param authService handles the actual login business logic
     */
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Handles user login requests.
     *
     * Accepts email and password, verifies them, and returns a JWT token
     * along with user details — all wrapped in a standard ApiResponse.
     *
     * The @Valid annotation triggers validation on LoginRequestDto before
     * this method even runs — blank email or missing password returns
     * a 400 automatically.
     *
     * Response scenarios:
     * - 200 OK           → credentials valid, token returned in data field
     * - 400 Bad Request  → email/password missing or invalid format
     * - 401 Unauthorized → wrong email or password
     * - 403 Forbidden    → account exists but is not activated yet
     * - 500 Server Error → something unexpected went wrong
     *
     * @param request the login request body containing email and password
     * @return ResponseEntity wrapping ApiResponse with token on success
     *         or an error message on failure
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
            /*
             * Account exists but is not activated yet.
             * Common for panel members who haven't set their password.
             * 403 is correct here — it's not wrong credentials,
             * it's an account status issue.
             */
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));

        } catch (BadCredentialsException e) {
            /*
             * Wrong email or wrong password.
             * We give the same message for both intentionally —
             * telling which one is wrong helps attackers.
             */
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            /*
             * Catch-all for anything unexpected.
             * We never expose internal details to the client —
             * the real error will show up in the application logs.
             */
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            "Something went wrong. Please try again later."
                    ));
        }
    }
}