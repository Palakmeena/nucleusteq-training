package com.nucleusteq.interviewtracker.controller;

import com.nucleusteq.interviewtracker.dto.InterviewRequestDto;
import com.nucleusteq.interviewtracker.dto.InterviewResponseDto;
import com.nucleusteq.interviewtracker.service.InterviewService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for interview scheduling and retrieval.
 * HR schedules interviews, panel members view their assignments.
 */
@RestController
public class InterviewController {

    private final InterviewService interviewService;

    /**
     * Constructor injection — keeps dependencies explicit and testable.
     *
     * @param interviewService handles all interview business logic
     */
    @Autowired
    public InterviewController(final InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    /**
     * HR schedules a new interview for a candidate.
     * Validates stage, checks for duplicates, assigns panel members.
     * POST /hr/interview
     *
     * @param request the interview scheduling details from HR
     * @return 201 Created with scheduled interview details
     */
    @PostMapping("/hr/interview")
    public ResponseEntity<ApiResponse<InterviewResponseDto>> scheduleInterview(
            @Valid @RequestBody final InterviewRequestDto request) {

        try {
            InterviewResponseDto response =
                    interviewService.scheduleInterview(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            "Interview scheduled successfully", response
                    ));
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
     * HR views all interviews scheduled for a specific candidate.
     * Shows full interview history including stage and panel details.
     * GET /hr/interview/candidate/{candidateId}
     *
     * @param candidateId the ID of the candidate
     * @return 200 OK with list of all interviews for this candidate
     */
    @GetMapping("/hr/interview/candidate/{candidateId}")
    public ResponseEntity<ApiResponse<List<InterviewResponseDto>>> getInterviewsByCandidate(
            @PathVariable final Long candidateId) {

        try {
            List<InterviewResponseDto> response =
                    interviewService.getInterviewsByCandidate(candidateId);
            return ResponseEntity.ok(
                    ApiResponse.success("Interviews fetched successfully", response)
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
     * HR views a single interview by its ID.
     * GET /hr/interview/{id}
     *
     * @param id the interview's database ID
     * @return 200 OK with interview details, or 404 if not found
     */
    @GetMapping("/hr/interview/{id}")
    public ResponseEntity<ApiResponse<InterviewResponseDto>> getInterviewById(
            @PathVariable final Long id) {

        try {
            InterviewResponseDto response =
                    interviewService.getInterviewById(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Interview fetched successfully", response)
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
     * Panel member views all interviews assigned to them.
     * Email extracted from JWT — panel can only see their own interviews.
     * GET /panel/interviews
     *
     * @param authentication Spring Security injects this automatically
     * @return 200 OK with list of interviews assigned to this panel member
     */
    @GetMapping("/panel/interviews")
    public ResponseEntity<ApiResponse<List<InterviewResponseDto>>> getInterviewsForPanel(
            final Authentication authentication) {

        try {
            String email = authentication.getName();
            List<InterviewResponseDto> response =
                    interviewService.getInterviewsForPanel(email);
            return ResponseEntity.ok(
                    ApiResponse.success("Interviews fetched successfully", response)
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
     * Candidate views their own interview schedule.
     * Shows date, time and panel names — no feedback visible per SRS.
     * GET /candidate/interviews
     *
     * @param authentication Spring Security injects this automatically
     * @return 200 OK with candidate's own interview schedule
     */
    @GetMapping("/candidate/interviews")
    public ResponseEntity<ApiResponse<List<InterviewResponseDto>>> getInterviewsForCandidate(
            final Authentication authentication) {

        try {
            /*
             * We reuse getInterviewsForPanel logic pattern here —
             * load user by email from JWT, find their candidate profile,
             * then fetch all interviews for that candidate.
             */
            String email = authentication.getName();
            List<InterviewResponseDto> response =
                    interviewService.getInterviewsForCandidate(email);
            return ResponseEntity.ok(
                    ApiResponse.success("Your interview schedule fetched successfully", response)
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