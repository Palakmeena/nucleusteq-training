package com.nucleusteq.interviewtracker.controller;

import com.nucleusteq.interviewtracker.dto.CandidateRequestDto;
import com.nucleusteq.interviewtracker.dto.CandidateResponseDto;
import com.nucleusteq.interviewtracker.service.CandidateService;
import com.nucleusteq.interviewtracker.service.GoogleDriveService;
import com.nucleusteq.interviewtracker.util.ApiResponse;
import jakarta.validation.Valid;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for candidate profiling operations.
 * Handles self-registration by candidates and HR-side candidate management.
 */
@RestController
public class CandidateController {

    private final CandidateService candidateService;
    private final GoogleDriveService googleDriveService;

    /**
     * Constructor injection — keeps dependencies explicit and testable.
     *
     * @param candidateService handles all candidate business logic
     * @param googleDriveService handles cloud storage uploads
     */
    @Autowired
    public CandidateController(final CandidateService candidateService, final GoogleDriveService googleDriveService) {
        this.candidateService = candidateService;
        this.googleDriveService = googleDriveService;
    }

    /**
     * Candidate submits their own profiling form.
     * This is a public endpoint — no token needed to apply.
     * POST /candidate/register
     *
     * @param request the candidate profiling form data
     * @return 201 Created with saved candidate profile
     */
    @PostMapping("/candidate/register")
    public ResponseEntity<ApiResponse<CandidateResponseDto>> registerCandidate(
            @Valid @RequestBody final CandidateRequestDto request,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("You must be logged in to apply for a job."));
        }

        try {
            CandidateResponseDto response =
                    candidateService.createCandidateProfile(request, authentication.getName());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Profile created successfully", response));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
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
     * HR creates a candidate profile manually on behalf of a candidate.
     * Only HR can access this — protected by /hr/** rule in SecurityConfig.
     * POST /hr/candidate
     *
     * @param request the candidate profiling form data filled by HR
     * @return 201 Created with saved candidate profile
     */
    @PostMapping("/hr/candidate")
    public ResponseEntity<ApiResponse<CandidateResponseDto>> createCandidateByHr(
            @Valid @RequestBody final CandidateRequestDto request) {

        try {
            CandidateResponseDto response =
                    candidateService.createCandidateProfileByHr(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Candidate profile created successfully", response));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
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
     * HR views all candidates in the system.
     * Only HR can access this — protected by /hr/** rule.
     * GET /hr/candidates
     *
     * @return 200 OK with list of all candidates
     */
    @GetMapping("/hr/candidates")
    public ResponseEntity<ApiResponse<List<CandidateResponseDto>>> getAllCandidates() {
        try {
            List<CandidateResponseDto> response =
                    candidateService.getAllCandidates();
            return ResponseEntity.ok(
                    ApiResponse.success("Candidates fetched successfully", response)
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Something went wrong. Please try again later."));
        }
    }

    /**
     * HR views a single candidate's full details by ID.
     * Only HR can access this — protected by /hr/** rule.
     * GET /hr/candidate/{id}
     *
     * @param id the candidate's database ID
     * @return 200 OK with candidate details, or 404 if not found
     */
    @GetMapping("/hr/candidate/{id}")
    public ResponseEntity<ApiResponse<CandidateResponseDto>> getCandidateById(
            @PathVariable final Long id) {

        try {
            CandidateResponseDto response =
                    candidateService.getCandidateById(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Candidate fetched successfully", response)
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
     * Logged-in candidate views their own profile.
     * Email is extracted from the JWT token — candidate can't see others.
     * GET /candidate/profile
     *
     * @param authentication Spring Security injects this automatically
     * @return 200 OK with the candidate's own profile
     */
    @GetMapping("/candidate/profile")
    public ResponseEntity<ApiResponse<CandidateResponseDto>> getCandidateProfile(
            final Authentication authentication) {

        try {
            /*
             * We get the email from the Authentication object which Spring
             * Security populates from the JWT token in JwtFilter.
             * This way the candidate can only ever see their own profile.
             */
            String email = authentication.getName();
            CandidateResponseDto response =
                    candidateService.getCandidateProfile(email);
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

    /**
     * Candidate uploads their resume — PDF only, max 5MB.
     * File is saved locally and path is stored in candidate profile.
     * POST /candidate/resume/{candidateId}
     *
     * @param candidateId the ID of the candidate uploading the resume
     * @param file        the uploaded PDF file
     * @return 200 OK with success message, or 400 if file is invalid
     */
    @PostMapping("/candidate/resume/{candidateId}")
    public ResponseEntity<ApiResponse<Void>> uploadResume(
            @PathVariable final Long candidateId,
            @RequestParam("file") final MultipartFile file) {

        try {
            /*
             * Validate file is not empty and is a PDF.
             * We check content type rather than extension — more reliable.
             */
            if (file.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Please select a file to upload"));
            }

            if (!"application/pdf".equals(file.getContentType())) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Only PDF files are allowed"));
            }

            // max 5MB size check — 5 * 1024 * 1024 bytes
            final long maxFileSize = 5 * 1024 * 1024;
            if (file.getSize() > maxFileSize) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("File size cannot exceed 5MB"));
            }

            /*
             * Upload the file to Google Drive (Mocked).
             * The service returns the webViewLink which we store as resumePath.
             */
            String driveUrl = googleDriveService.uploadFile(file);

            candidateService.updateResumePath(candidateId, driveUrl);

            return ResponseEntity.ok(
                    ApiResponse.success("Resume uploaded successfully", null)
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
     * HR updates the current stage of a candidate.
     * PUT /hr/candidate/{id}/stage
     *
     * @param id    the candidate ID
     * @param stage the new interview stage
     * @return 200 OK with the updated candidate
     */
    @org.springframework.web.bind.annotation.PutMapping("/hr/candidate/{id}/stage")
    public ResponseEntity<ApiResponse<CandidateResponseDto>> updateCandidateStage(
            @PathVariable final Long id,
            @RequestParam final com.nucleusteq.interviewtracker.enums.InterviewStage stage) {

        try {
            CandidateResponseDto response = candidateService.updateCandidateStage(id, stage);
            return ResponseEntity.ok(
                    ApiResponse.success("Candidate stage updated successfully", response)
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