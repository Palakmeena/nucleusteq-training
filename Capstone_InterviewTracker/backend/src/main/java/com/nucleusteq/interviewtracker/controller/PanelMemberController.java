package com.nucleusteq.interviewtracker.controller;

import com.nucleusteq.interviewtracker.dto.PanelMemberRequestDto;
import com.nucleusteq.interviewtracker.dto.PanelMemberResponseDto;
import com.nucleusteq.interviewtracker.service.PanelMemberService;
import com.nucleusteq.interviewtracker.service.CandidateService;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.enums.UserRole;
import com.nucleusteq.interviewtracker.util.ApiResponse;
import com.nucleusteq.interviewtracker.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for panel member management.
 * Handles HR-side panel creation and panel member activation.
 */
@RestController
public class PanelMemberController {

        private final PanelMemberService panelMemberService;
        private final CandidateService candidateService;
        private final UserRepository userRepository;
        private static final Logger logger = LoggerFactory.getLogger(PanelMemberController.class);

        /**
         * Constructor injection — keeps dependencies explicit and testable.
         *
         * @param panelMemberService handles all panel member business logic
         */
        @Autowired
        public PanelMemberController(final PanelMemberService panelMemberService,
                        final CandidateService candidateService,
                        final UserRepository userRepository) {
                this.panelMemberService = panelMemberService;
                this.candidateService = candidateService;
                this.userRepository = userRepository;
        }

        /**
         * HR creates a new panel member account.
         * Generates activation token — panel member activates via email link.
         * POST /hr/panel
         *
         * @param request the panel member details from HR
         * @return 201 Created with saved panel member details
         */
        @PostMapping("/hr/panel")
        public ResponseEntity<ApiResponse<PanelMemberResponseDto>> createPanelMember(
                        @Valid @RequestBody final PanelMemberRequestDto request) {

                try {
                        PanelMemberResponseDto response = panelMemberService.createPanelMember(request);

                        String message = response.isActivationEmailSent()
                                        ? "Panel member created successfully. Activation link has been sent to their email."
                                        : "Panel member created, but activation email could not be sent. Share the activation link manually.";

                        return ResponseEntity
                                        .status(HttpStatus.CREATED)
                                        .body(ApiResponse.success(
                                                        message,
                                                        response));
                } catch (IllegalArgumentException e) {
                        logger.warn("Invalid panel member create request: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
                } catch (Exception e) {
                        logger.error("Unexpected error creating panel member", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
                }
        }

        /**
         * HR views all panel members in the system.
         * Shows both active and inactive panel members.
         * GET /hr/panels
         *
         * @return 200 OK with list of all panel members
         */
        @GetMapping("/hr/panels")
        public ResponseEntity<ApiResponse<List<PanelMemberResponseDto>>> getAllPanelMembers() {
                try {
                        List<PanelMemberResponseDto> response = panelMemberService.getAllPanelMembers();
                        return ResponseEntity.ok(
                                        ApiResponse.success("Panel members fetched successfully", response));
                } catch (Exception e) {
                        logger.error("Unexpected error fetching panel members", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
                }
        }

        /**
         * HR views a single panel member by ID.
         * GET /hr/panel/{id}
         *
         * @param id the panel member's database ID
         * @return 200 OK with panel member details, or 404 if not found
         */
        @GetMapping("/hr/panel/{id}")
        public ResponseEntity<ApiResponse<PanelMemberResponseDto>> getPanelMemberById(
                        @PathVariable final Long id) {

                try {
                        PanelMemberResponseDto response = panelMemberService.getPanelMemberById(id);
                        return ResponseEntity.ok(
                                        ApiResponse.success("Panel member fetched successfully", response));
                } catch (jakarta.persistence.EntityNotFoundException e) {
                        logger.warn("Panel member not found: {}", id);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
                } catch (Exception e) {
                        logger.error("Unexpected error fetching panel member {}", id, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
                }
        }

        /**
         * Activates account (panel member or candidate) using the token from email.
         * Public endpoint — panel member doesn't have a token yet at this point.
         * POST /auth/activate?token=xxx&password=xxx
         *
         * @param token    the activation token from the email link
         * @param password the new password chosen by the user
         * @return 200 OK on success, or 400 if token is invalid or expired
         */
        @PostMapping(AppConstants.AUTH_BASE + AppConstants.ACTIVATE)
        public ResponseEntity<ApiResponse<Void>> activateAccount(
                        @RequestParam final String token,
                        @RequestParam @NotBlank(message = AppConstants.PASSWORD_REQUIRED) @Size(min = 6, message = "Password must be at least 6 characters") final String password) {

                try {
                        // If password was Base64-obfuscated by frontend, decode it here
                        // (backwards-compatible)
                        String decodedPassword = password;
                        try {
                                if (password != null && password.matches("^[A-Za-z0-9+/=]+$")) {
                                        decodedPassword = new String(java.util.Base64.getDecoder().decode(password),
                                                        java.nio.charset.StandardCharsets.UTF_8);
                                }
                        } catch (IllegalArgumentException ignored) {
                                // leave password as-is
                        }

                        // Find the user by token to determine their role
                        User user = userRepository.findByActivationToken(token)
                                        .orElseThrow(() -> new IllegalArgumentException("Invalid activation token"));

                        // Route to appropriate activation method based on role
                        if (user.getRole() == UserRole.PANEL) {
                                panelMemberService.activatePanelMember(token, decodedPassword);
                        } else if (user.getRole() == UserRole.CANDIDATE) {
                                candidateService.activateCandidateAccount(token, decodedPassword);
                        } else {
                                throw new IllegalArgumentException("Invalid user role for activation");
                        }

                        return ResponseEntity.ok(
                                        ApiResponse.success(
                                                        "Account activated successfully. You can now log in.",
                                                        null));
                } catch (IllegalArgumentException e) {
                        logger.warn("Activation failed: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
                } catch (Exception e) {
                        logger.error("Unexpected error during activation", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
                }
        }

        /**
         * Logged-in panel member views their own profile.
         * Email extracted from JWT — panel member can't see others.
         * GET /panel/profile
         *
         * @param authentication Spring Security injects this automatically
         * @return 200 OK with the panel member's own profile
         */
        @GetMapping("/panel/profile")
        public ResponseEntity<ApiResponse<PanelMemberResponseDto>> getPanelMemberProfile(
                        final Authentication authentication) {

                try {
                        String email = authentication.getName();
                        PanelMemberResponseDto response = panelMemberService.getPanelMemberProfile(email);
                        return ResponseEntity.ok(
                                        ApiResponse.success("Profile fetched successfully", response));
                } catch (jakarta.persistence.EntityNotFoundException e) {
                        logger.warn("Panel profile not found for: {}",
                                        authentication != null ? authentication.getName() : "unknown");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
                } catch (Exception e) {
                        logger.error("Unexpected error fetching panel profile", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
                }
        }

        /**
         * HR updates an existing panel member's details.
         * PUT /hr/panel/{id}
         *
         * @param id      the panel member ID
         * @param request the updated details
         * @return 200 OK with the updated panel member
         */
        @org.springframework.web.bind.annotation.PutMapping("/hr/panel/{id}")
        public ResponseEntity<ApiResponse<PanelMemberResponseDto>> updatePanelMember(
                        @PathVariable final Long id,
                        @Valid @RequestBody final PanelMemberRequestDto request) {

                try {
                        PanelMemberResponseDto response = panelMemberService.updatePanelMember(id, request);
                        return ResponseEntity.ok(
                                        ApiResponse.success("Panel member updated successfully", response));
                } catch (jakarta.persistence.EntityNotFoundException e) {
                        logger.warn("Panel member update failed - not found: {}", id);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
                } catch (Exception e) {
                        logger.error("Unexpected error updating panel member {}", id, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
                }
        }

        /**
         * HR removes a panel member from the system.
         * DELETE /hr/panel/{id}
         *
         * @param id the panel member ID
         * @return 200 OK with success message
         */
        @org.springframework.web.bind.annotation.DeleteMapping("/hr/panel/{id}")
        public ResponseEntity<ApiResponse<Void>> deletePanelMember(
                        @PathVariable final Long id) {

                try {
                        panelMemberService.deletePanelMember(id);
                        return ResponseEntity.ok(
                                        ApiResponse.success("Panel member removed successfully", null));
                } catch (IllegalStateException e) {
                        logger.warn("Panel member delete blocked for {}: {}", id, e.getMessage());
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(e.getMessage()));
                } catch (jakarta.persistence.EntityNotFoundException e) {
                        logger.warn("Panel member delete failed - not found: {}", id);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
                } catch (Exception e) {
                        logger.error("Unexpected error deleting panel member {}", id, e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
                }
        }
}