package com.nucleusteq.interviewtracker.util;

/**
 * Application-wide constants for API endpoints, roles, and messages.
 * Centralizes all hardcoded strings for easier maintenance.
 */
public final class AppConstants {

    private AppConstants() {}

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTH_HEADER = "Authorization";
    public static final String ROLE_HR = "HR";
    public static final String ROLE_CANDIDATE = "CANDIDATE";
    public static final String ROLE_PANEL = "PANEL";
    public static final String AUTH_BASE = "/auth";
    public static final String LOGIN = "/login";
    public static final String SIGNUP = "/signup";
    public static final String ACTIVATE = "/activate";

    public static final String HR_INTERVIEW = "/hr/interview";
    public static final String HR_INTERVIEW_CANDIDATE = "/hr/interview/candidate/{candidateId}";
    public static final String HR_INTERVIEW_BY_ID = "/hr/interview/{id}";
    public static final String PANEL_INTERVIEWS = "/panel/interviews";
    public static final String CANDIDATE_INTERVIEWS = "/candidate/interviews";
    public static final String PANEL_INTERVIEW_FEEDBACK = "/panel/interview/{id}/feedback";
    public static final String HR_INTERVIEW_HR_FEEDBACK = "/hr/interview/{id}/hr-feedback";

    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String INVALID_EMAIL = "Please provide a valid email address";
    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String SOMETHING_WENT_WRONG = "Something went wrong. Please try again later.";
    public static final String LOGIN_SUCCESS = "Login successful";
    public static final String ACCOUNT_CREATED = "Account created successfully. Please check your email for the activation link.";
    public static final String INTERVIEW_SCHEDULED = "Interview scheduled successfully";
    public static final String INTERVIEWS_FETCHED = "Interviews fetched successfully";
    public static final String INTERVIEW_FETCHED = "Interview fetched successfully";
    public static final String FEEDBACK_SUBMITTED = "Feedback submitted successfully";
    public static final String HR_FEEDBACK_SUBMITTED = "HR feedback submitted successfully";
    public static final String FAILED_TO_SUBMIT_FEEDBACK = "Failed to submit feedback: ";
}