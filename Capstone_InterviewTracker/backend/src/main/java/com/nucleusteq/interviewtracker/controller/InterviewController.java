package com.nucleusteq.interviewtracker.controller;

import com.nucleusteq.interviewtracker.dto.InterviewRequestDto;
import com.nucleusteq.interviewtracker.dto.InterviewResponseDto;
import com.nucleusteq.interviewtracker.dto.FeedbackRequestDto;
import com.nucleusteq.interviewtracker.service.InterviewService;
import com.nucleusteq.interviewtracker.util.ApiResponse;
import com.nucleusteq.interviewtracker.util.AppConstants;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for interview scheduling and feedback.
 * Manages interview creation, scheduling, and panel feedback submission.
 */
@RestController
public class InterviewController {

    private static final Logger logger = LoggerFactory.getLogger(InterviewController.class);

    private final InterviewService interviewService;

    @Autowired
    public InterviewController(final InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping(AppConstants.HR_INTERVIEW)
    public ResponseEntity<ApiResponse<InterviewResponseDto>> scheduleInterview(
            @Valid @RequestBody final InterviewRequestDto request) {

        try {
            InterviewResponseDto response = interviewService.scheduleInterview(request);
            logger.info("Interview scheduled: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(AppConstants.INTERVIEW_SCHEDULED, response));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid interview request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            logger.warn("Entity not found while scheduling interview: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error scheduling interview", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
        }
    }

    @GetMapping(AppConstants.HR_INTERVIEW_CANDIDATE)
    public ResponseEntity<ApiResponse<List<InterviewResponseDto>>> getInterviewsByCandidate(
            @PathVariable final Long candidateId) {

        try {
            List<InterviewResponseDto> response = interviewService.getInterviewsByCandidate(candidateId);
            return ResponseEntity.ok(ApiResponse.success(AppConstants.INTERVIEWS_FETCHED, response));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            logger.warn("Candidate not found: {}", candidateId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error fetching interviews for candidate {}", candidateId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
        }
    }

    @GetMapping(AppConstants.HR_INTERVIEW_BY_ID)
    public ResponseEntity<ApiResponse<InterviewResponseDto>> getInterviewById(@PathVariable final Long id) {
        try {
            InterviewResponseDto response = interviewService.getInterviewById(id);
            return ResponseEntity.ok(ApiResponse.success(AppConstants.INTERVIEW_FETCHED, response));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            logger.warn("Interview not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error fetching interview {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
        }
    }

    @GetMapping(AppConstants.PANEL_INTERVIEWS)
    public ResponseEntity<ApiResponse<List<InterviewResponseDto>>> getInterviewsForPanel(
            final Authentication authentication) {

        try {
            String email = authentication.getName();
            List<InterviewResponseDto> response = interviewService.getInterviewsForPanel(email);
            return ResponseEntity.ok(ApiResponse.success(AppConstants.INTERVIEWS_FETCHED, response));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            logger.warn("Panel interviews not found for: {}", authentication.getName());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error fetching panel interviews", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
        }
    }

    @GetMapping(AppConstants.CANDIDATE_INTERVIEWS)
    public ResponseEntity<ApiResponse<List<InterviewResponseDto>>> getInterviewsForCandidate(
            final Authentication authentication) {

        try {
            String email = authentication.getName();
            List<InterviewResponseDto> response = interviewService.getInterviewsForCandidate(email);
            return ResponseEntity.ok(ApiResponse.success("Your interview schedule fetched successfully", response));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            logger.warn("Candidate interviews not found for: {}", authentication.getName());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error fetching candidate interviews", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.SOMETHING_WENT_WRONG));
        }
    }

    @PutMapping(AppConstants.PANEL_INTERVIEW_FEEDBACK)
    public ResponseEntity<ApiResponse<Void>> submitFeedback(
            @PathVariable final Long id,
            @Valid @RequestBody final FeedbackRequestDto request,
            final Authentication authentication) {

        try {
            String email = authentication.getName();
            interviewService.submitFeedback(id, request, email);
            logger.info("Feedback submitted for interview {} by {}", id, email);
            return ResponseEntity.ok(ApiResponse.success(AppConstants.FEEDBACK_SUBMITTED, null));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            logger.warn("Failed to submit feedback - not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Failed to submit feedback for {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.FAILED_TO_SUBMIT_FEEDBACK + e.getMessage()));
        }
    }

    @PutMapping(AppConstants.HR_INTERVIEW_HR_FEEDBACK)
    public ResponseEntity<ApiResponse<Void>> submitHrFeedback(
            @PathVariable final Long id,
            @Valid @RequestBody final FeedbackRequestDto request) {

        try {
            interviewService.submitHrFeedback(id, request);
            logger.info("HR feedback submitted for interview {}", id);
            return ResponseEntity.ok(ApiResponse.success(AppConstants.HR_FEEDBACK_SUBMITTED, null));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid HR feedback for {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            logger.warn("HR feedback failed - not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Failed to submit HR feedback for {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.FAILED_TO_SUBMIT_FEEDBACK + e.getMessage()));
        }
    }
}