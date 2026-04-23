package com.nucleusteq.interviewtracker.util;

/**
 * Central place for all constant values used across the application.
 * Instead of hardcoding strings like "Bearer " or role names in multiple
 * places, we define them once here and reference them everywhere.
 * This way if something changes, you update it in one place only.
 */
public final class AppConstants {

    /**
     * Private constructor — this is a utility class and should
     * never be instantiated. All fields are static.
     */
    private AppConstants() {
    }

    // JWT related constants
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTH_HEADER = "Authorization";

    // Role name constants — used in @PreAuthorize annotations
    public static final String ROLE_HR = "HR";
    public static final String ROLE_CANDIDATE = "CANDIDATE";
    public static final String ROLE_PANEL = "PANEL";

    // Activation token expiry in hours
    public static final long ACTIVATION_TOKEN_EXPIRY_HOURS = 24;

    // Generic response messages
    public static final String SUCCESS = "Success";
    public static final String SOMETHING_WENT_WRONG = "Something went wrong. Please try again later.";
    public static final String UNAUTHORIZED_ACCESS = "You are not authorized to perform this action.";
}