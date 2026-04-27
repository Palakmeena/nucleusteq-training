package com.nucleusteq.interviewtracker.controller;

import com.nucleusteq.interviewtracker.dto.InterviewRequestDto;
import com.nucleusteq.interviewtracker.dto.InterviewResponseDto;
import com.nucleusteq.interviewtracker.dto.FeedbackRequestDto;
import com.nucleusteq.interviewtracker.service.InterviewService;
import com.nucleusteq.interviewtracker.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for interview scheduling and retrieval.
 * HR schedules interviews, panel members view their assignments.
 */
@RestController
public class InterviewController {

    private final InterviewService interviewService;

    @Autowired
    public InterviewController(final InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping("/hr/interview")
    public ResponseEntity<ApiResponse<InterviewResponseDto>> scheduleInterview(
            @Valid @RequestBody final InterviewRequestDto request) {

        try {
            InterviewResponseDto response = interviewService.scheduleInterview(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Interview scheduled successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Something went wrong. Please try again later."));
        }
    }

    @GetMapping("/hr/interview/candidate/{candidateId}")
    public ResponseEntity<ApiResponse<List<InterviewResponseDto>>> getInterviewsByCandidate(
            @PathVariable final Long candidateId) {

        try {
            List<InterviewResponseDto> response = interviewService.getInterviewsByCandidate(candidateId);
            return ResponseEntity.ok(ApiResponse.success("Interviews fetched successfully", response));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Something went wrong. Please try again later."));
        }
    }

    @GetMapping("/hr/interview/{id}")
    public ResponseEntity<ApiResponse<InterviewResponseDto>> getInterviewById(@PathVariable final Long id) {
        try {
            InterviewResponseDto response = interviewService.getInterviewById(id);
            return ResponseEntity.ok(ApiResponse.success("Interview fetched successfully", response));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Something went wrong. Please try again later."));
        }
    }

    @GetMapping("/panel/interviews")
    public ResponseEntity<ApiResponse<List<InterviewResponseDto>>> getInterviewsForPanel(
            final Authentication authentication) {

        try {
            String email = authentication.getName();
            List<InterviewResponseDto> response = interviewService.getInterviewsForPanel(email);
            return ResponseEntity.ok(ApiResponse.success("Interviews fetched successfully", response));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Something went wrong. Please try again later."));
        }
    }

    @GetMapping("/candidate/interviews")
    public ResponseEntity<ApiResponse<List<InterviewResponseDto>>> getInterviewsForCandidate(
            final Authentication authentication) {

        try {
            String email = authentication.getName();
            List<InterviewResponseDto> response = interviewService.getInterviewsForCandidate(email);
            return ResponseEntity.ok(ApiResponse.success("Your interview schedule fetched successfully", response));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Something went wrong. Please try again later."));
        }
    }

    @PutMapping("/panel/interview/{id}/feedback")
    public ResponseEntity<ApiResponse<Void>> submitFeedback(
            @PathVariable final Long id,
            @Valid @RequestBody final FeedbackRequestDto request,
            final Authentication authentication) {

        try {
            String email = authentication.getName();
            interviewService.submitFeedback(id, request, email);
            return ResponseEntity.ok(ApiResponse.success("Feedback submitted successfully", null));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to submit feedback: " + e.getMessage()));
        }
    }
}