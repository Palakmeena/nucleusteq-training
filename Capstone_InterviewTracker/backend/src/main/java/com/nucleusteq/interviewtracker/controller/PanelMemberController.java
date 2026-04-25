package com.nucleusteq.interviewtracker.controller;

import com.nucleusteq.interviewtracker.dto.PanelMemberRequestDto;
import com.nucleusteq.interviewtracker.dto.PanelMemberResponseDto;
import com.nucleusteq.interviewtracker.service.PanelMemberService;
import com.nucleusteq.interviewtracker.util.ApiResponse;
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

    /**
     * Constructor injection — keeps dependencies explicit and testable.
     *
     * @param panelMemberService handles all panel member business logic
     */
    @Autowired
    public PanelMemberController(final PanelMemberService panelMemberService) {
        this.panelMemberService = panelMemberService;
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
            PanelMemberResponseDto response =
                    panelMemberService.createPanelMember(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            "Panel member created successfully. "
                            + "Activation link has been sent to their email.",
                            response
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Something went wrong. Please try again later."));
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
            List<PanelMemberResponseDto> response =
                    panelMemberService.getAllPanelMembers();
            return ResponseEntity.ok(
                    ApiResponse.success("Panel members fetched successfully", response)
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Something went wrong. Please try again later."));
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
            PanelMemberResponseDto response =
                    panelMemberService.getPanelMemberById(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Panel member fetched successfully", response)
            );
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Something went wrong. Please try again later."));
        }
    }

    /**
     * Panel member activates their account using the token from email.
     * Public endpoint — panel member doesn't have a token yet at this point.
     * POST /auth/activate?token=xxx&password=xxx
     *
     * @param token    the activation token from the email link
     * @param password the new password chosen by the panel member
     * @return 200 OK on success, or 400 if token is invalid or expired
     */
    @PostMapping("/auth/activate")
    public ResponseEntity<ApiResponse<Void>> activatePanelMember(
            @RequestParam final String token,
            @RequestParam @NotBlank(message = "Password is required")
            @Size(min = 6, message = "Password must be at least 6 characters")
            final String password) {

        try {
            panelMemberService.activatePanelMember(token, password);
            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Account activated successfully. You can now log in.",
                            null
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Something went wrong. Please try again later."));
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
            PanelMemberResponseDto response =
                    panelMemberService.getPanelMemberProfile(email);
            return ResponseEntity.ok(
                    ApiResponse.success("Profile fetched successfully", response)
            );
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Something went wrong. Please try again later."));
        }
    }
}