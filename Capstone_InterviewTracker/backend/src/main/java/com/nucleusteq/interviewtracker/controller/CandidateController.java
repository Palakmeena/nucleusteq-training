package com.nucleusteq.interviewtracker.controller;

import com.nucleusteq.interviewtracker.dto.CandidateRequestDto;
import com.nucleusteq.interviewtracker.dto.CandidateResponseDto;
import com.nucleusteq.interviewtracker.service.CandidateService;
import com.nucleusteq.interviewtracker.service.GoogleDriveService;
import com.nucleusteq.interviewtracker.util.ApiResponse;
import com.nucleusteq.interviewtracker.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Objects;

/**
 * REST controller for candidate profiling operations.
 * Handles self-registration by candidates and HR-side candidate management.
 */
@RestController
public class CandidateController {

    private final CandidateService candidateService;
    private final GoogleDriveService googleDriveService;
    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    /**
     * Constructor injection — keeps dependencies explicit and testable.
     *
     * @param candidateService handles all candidate business logic
     * @param googleDriveService handles cloud storage uploads
     */
    @Autowired
    public CandidateController(final CandidateService candidateService, 
                               final GoogleDriveService googleDriveService) {
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

        if (Objects.isNull(authentication)) {
            logger.warn("Unauthorized attempt to register candidate");
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
            logger.warn("Invalid candidate registration request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            logger.warn("Entity not found during candidate registration: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during candidate registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(com.nucleusteq.interviewtracker.util.AppConstants.SOMETHING_WENT_WRONG));
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
            logger.error("Unexpected error fetching candidates", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(com.nucleusteq.interviewtracker.util.AppConstants.SOMETHING_WENT_WRONG));
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
            logger.error("Unexpected error fetching all candidates", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(com.nucleusteq.interviewtracker.util.AppConstants.SOMETHING_WENT_WRONG));
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
            logger.warn("Candidate not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error fetching candidate {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(com.nucleusteq.interviewtracker.util.AppConstants.SOMETHING_WENT_WRONG));
        }
    }

    /**
     * Returns the candidate's applied application snapshot for dashboard and locking logic.
     */
    @GetMapping("/candidate/application")
    public ResponseEntity<ApiResponse<CandidateResponseDto>> getCandidateApplication(
            final Authentication authentication) {
        try {
            if (Objects.isNull(authentication)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Unauthorized request. Please login first."));
            }

            String email = authentication.getName();
            return ResponseEntity.ok(ApiResponse.success("Application fetched", candidateService.getCandidateProfile(email)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Uploads resume for a candidate application snapshot by candidate ID.
     * Used by application forms that create profile first and upload resume second.
     */
    @PostMapping("/candidate/resume/{candidateId}")
    public ResponseEntity<ApiResponse<String>> uploadResumeByCandidateId(
            @PathVariable final Long candidateId,
            @RequestParam("file") final MultipartFile file) {

        try {
            if (file.isEmpty() || !"application/pdf".equals(file.getContentType())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Invalid PDF file"));
            }

            String driveUrl = googleDriveService.uploadFile(file);
            candidateService.updateResumePath(candidateId, driveUrl);

            return ResponseEntity.ok(ApiResponse.success("Resume uploaded successfully", driveUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
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
            logger.warn("Candidate stage update failed - not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error updating candidate stage {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(com.nucleusteq.interviewtracker.util.AppConstants.SOMETHING_WENT_WRONG));
        }
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/hr/candidate/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCandidate(@PathVariable final Long id) {
        try {
            candidateService.deleteCandidate(id);
            return ResponseEntity.ok(ApiResponse.success("Candidate deleted successfully", null));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            logger.warn("Candidate delete failed - not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error deleting candidate {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(com.nucleusteq.interviewtracker.util.AppConstants.SOMETHING_WENT_WRONG));
        }
    }
}