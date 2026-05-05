package com.nucleusteq.interviewtracker.controller;

import com.nucleusteq.interviewtracker.dto.JobDescriptionRequestDto;
import com.nucleusteq.interviewtracker.dto.JobDescriptionResponseDto;
import com.nucleusteq.interviewtracker.service.JobDescriptionService;
import com.nucleusteq.interviewtracker.util.ApiResponse;
import com.nucleusteq.interviewtracker.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

/**
 * REST controller for Job Description management.
 */
@RestController
public class JobDescriptionController {

    private final JobDescriptionService jobDescriptionService;
    private static final Logger logger = LoggerFactory.getLogger(JobDescriptionController.class);

    /**
     * Constructor injection — keeps dependencies explicit and testable.
     *
     * @param jobDescriptionService handles all JD business logic
     */
    @Autowired
    public JobDescriptionController(final JobDescriptionService jobDescriptionService) {
        this.jobDescriptionService = jobDescriptionService;
    }

    /**
     * Creates a new Job Description.
     * Only HR can access this endpoint — protected by /hr/** rule in SecurityConfig.
     */
    @PostMapping("/hr/jd")
    public ResponseEntity<ApiResponse<JobDescriptionResponseDto>> createJobDescription(
            @Valid @RequestBody final JobDescriptionRequestDto request) {

        try {
            JobDescriptionResponseDto response =
                    jobDescriptionService.createJobDescription(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Job description created successfully", response));

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid job description create request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error creating job description", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
        }
    }

    /**
     * Returns all active job descriptions for the public homepage.
     */
    @GetMapping("/jd/all")
    public ResponseEntity<ApiResponse<List<JobDescriptionResponseDto>>> getAllActiveJds() {
        try {
            List<JobDescriptionResponseDto> response =
                    jobDescriptionService.getAllActiveJobDescriptions();

            return ResponseEntity.ok(
                    ApiResponse.success("Job descriptions fetched successfully", response)
            );
        } catch (Exception e) {
            logger.error("Unexpected error fetching active JDs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
        }
    }

    /**
     * Returns all job descriptions for the HR dashboard —
     * both active and inactive, sorted newest first.
     */
    @GetMapping("/hr/jd/all")
    public ResponseEntity<ApiResponse<List<JobDescriptionResponseDto>>> getAllJdsForHr() {
        try {
            List<JobDescriptionResponseDto> response =
                    jobDescriptionService.getAllJobDescriptionsForHr();

            return ResponseEntity.ok(
                    ApiResponse.success("All job descriptions fetched successfully", response)
            );
        } catch (Exception e) {
            logger.error("Unexpected error fetching all JDs for HR", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
        }
    }

    /**
     * Returns a single job description by its ID.
     */
    @GetMapping("/jd/{id}")
    public ResponseEntity<ApiResponse<JobDescriptionResponseDto>> getJdById(
            @PathVariable final Long id) {

        try {
            JobDescriptionResponseDto response =
                    jobDescriptionService.getJobDescriptionById(id);

            return ResponseEntity.ok(
                    ApiResponse.success("Job description fetched successfully", response)
            );
        } catch (jakarta.persistence.EntityNotFoundException e) {
            logger.warn("JD not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error fetching JD {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
        }
    }

    /**
     * Updates an existing job description.
     */
    @PutMapping("/hr/jd/{id}")
    public ResponseEntity<ApiResponse<JobDescriptionResponseDto>> updateJobDescription(
            @PathVariable final Long id,
            @Valid @RequestBody final JobDescriptionRequestDto request) {

        try {
            JobDescriptionResponseDto response =
                    jobDescriptionService.updateJobDescription(id, request);

            return ResponseEntity.ok(
                    ApiResponse.success("Job description updated successfully", response)
            );
        } catch (jakarta.persistence.EntityNotFoundException e) {
            logger.warn("JD update failed - not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid JD update for {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error updating JD {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
        }
    }

    /**
     * Hard deletes a job description from the database.
     */
    @DeleteMapping("/hr/jd/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJobDescription(
            @PathVariable final Long id) {

        try {
            jobDescriptionService.deleteJobDescription(id);

            return ResponseEntity.ok(
                    ApiResponse.success("Job description deleted successfully", null)
            );
        } catch (jakarta.persistence.EntityNotFoundException e) {
            logger.warn("JD delete failed - not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            logger.warn("JD delete conflict for {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("Cannot delete job description because candidates have applied for it."));
        } catch (Exception e) {
            logger.error("Unexpected error deleting JD {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
        }
    }
}