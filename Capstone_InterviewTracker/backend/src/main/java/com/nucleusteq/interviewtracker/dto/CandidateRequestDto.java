package com.nucleusteq.interviewtracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO for creating a candidate profile.
 * Used by both HR and candidate to submit profiling form.
 */
public class CandidateRequestDto {

    /**
     * Full name of the candidate.
     * Cannot be blank.
     */
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    /**
     * Email address of the candidate.
     * Must be valid format and unique across all candidates.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    /**
     * Country code for mobile number e.g. +91.
     * Cannot be blank.
     */
    @NotBlank(message = "Mobile code is required")
    private String mobileCode;

    /**
     * Mobile number of the candidate.
     * Must be unique across all candidates.
     */
    @NotBlank(message = "Mobile number is required")
    @Size(min = 10, max = 15, message = "Mobile number must be between 10 and 15 digits")
    private String mobileNumber;

    /**
     * Date of birth of the candidate.
     * Optional field as per SRS.
     */
    private LocalDate dateOfBirth;

    /**
     * Current organization the candidate works at.
     * Cannot be blank.
     */
    @NotBlank(message = "Current organization is required")
    private String currentOrganization;

    /**
     * Total years of experience the candidate has.
     * Must be 0 or more.
     */
    @NotNull(message = "Total experience is required")
    @DecimalMin(value = "0.0", message = "Total experience cannot be negative")
    private Double totalExperience;

    /**
     * Relevant years of experience for the applied role.
     * Must be 0 or more.
     */
    @NotNull(message = "Relevant experience is required")
    @DecimalMin(value = "0.0", message = "Relevant experience cannot be negative")
    private Double relevantExperience;

    /**
     * Current CTC of the candidate in LPA.
     * Must be greater than 0.
     */
    @NotNull(message = "Current CTC is required")
    @DecimalMin(value = "0.0", message = "Current CTC cannot be negative")
    private Double currentCtc;

    /**
     * Expected CTC of the candidate in LPA.
     * Must be greater than 0.
     */
    @NotNull(message = "Expected CTC is required")
    @DecimalMin(value = "0.1", message = "Expected CTC must be greater than 0")
    private Double expectedCtc;

    /**
     * Notice period in days.
     * Must be 0 or more.
     */
    @NotNull(message = "Notice period is required")
    @Min(value = 0, message = "Notice period cannot be negative")
    private Integer noticePeriod;

    /**
     * Preferred job location of the candidate.
     * Cannot be blank.
     */
    @NotBlank(message = "Preferred location is required")
    private String preferredLocation;

    /**
     * Source through which candidate found the job e.g. LinkedIn, Naukri.
     * Cannot be blank.
     */
    @NotBlank(message = "Source is required")
    private String source;

    /**
     * ID of the job description the candidate is applying for.
     * HR selects from dropdown, candidate gets it auto-filled.
     */
    @NotNull(message = "Job description ID is required")
    private Long jobDescriptionId;

    /**
     * Default constructor needed for JSON deserialization.
     */
    public CandidateRequestDto() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getMobileCode() {
        return mobileCode;
    }

    public void setMobileCode(final String mobileCode) {
        this.mobileCode = mobileCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(final LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCurrentOrganization() {
        return currentOrganization;
    }

    public void setCurrentOrganization(final String currentOrganization) {
        this.currentOrganization = currentOrganization;
    }

    public Double getTotalExperience() {
        return totalExperience;
    }

    public void setTotalExperience(final Double totalExperience) {
        this.totalExperience = totalExperience;
    }

    public Double getRelevantExperience() {
        return relevantExperience;
    }

    public void setRelevantExperience(final Double relevantExperience) {
        this.relevantExperience = relevantExperience;
    }

    public Double getCurrentCtc() {
        return currentCtc;
    }

    public void setCurrentCtc(final Double currentCtc) {
        this.currentCtc = currentCtc;
    }

    public Double getExpectedCtc() {
        return expectedCtc;
    }

    public void setExpectedCtc(final Double expectedCtc) {
        this.expectedCtc = expectedCtc;
    }

    public Integer getNoticePeriod() {
        return noticePeriod;
    }

    public void setNoticePeriod(final Integer noticePeriod) {
        this.noticePeriod = noticePeriod;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(final String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public Long getJobDescriptionId() {
        return jobDescriptionId;
    }

    public void setJobDescriptionId(final Long jobDescriptionId) {
        this.jobDescriptionId = jobDescriptionId;
    }
}