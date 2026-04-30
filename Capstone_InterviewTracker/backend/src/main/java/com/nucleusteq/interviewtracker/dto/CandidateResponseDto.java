package com.nucleusteq.interviewtracker.dto;

import com.nucleusteq.interviewtracker.enums.InterviewStage;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for sending candidate profile data back to the client.
 * Candidates see limited fields, HR sees everything including current stage.
 */
public class CandidateResponseDto {

    /**
     * Database ID of the candidate.
     * Needed by HR to reference this candidate in other operations.
     */
    private Long id;

    /**
     * Full name of the candidate.
     */
    private String fullName;

    /**
     * Email address of the candidate.
     */
    private String email;

    /**
     * Country code for mobile number.
     */
    private String mobileCode;

    /**
     * Mobile number of the candidate.
     */
    private String mobileNumber;

    /**
     * Date of birth — optional field.
     */
    private LocalDate dateOfBirth;

    /**
     * Path to the uploaded resume file.
     * Null if resume not uploaded yet.
     */
    private String resumePath;

    /**
     * Current organization the candidate works at.
     */
    private String currentOrganization;

    /**
     * Total years of experience.
     */
    private Double totalExperience;

    /**
     * Relevant years of experience for this role.
     */
    private Double relevantExperience;

    /**
     * Current CTC in LPA.
     */
    private Double currentCtc;

    /**
     * Expected CTC in LPA.
     */
    private Double expectedCtc;

    /**
     * Notice period in days.
     */
    private Integer noticePeriod;

    /**
     * Preferred job location.
     */
    private String preferredLocation;

    /**
     * Source through which candidate found the job.
     */
    private String source;

    /**
     * Gender of the candidate.
     */
    private String gender;

    /**
     * Current stage of the candidate in the interview process.
     * Starts at PROFILING and moves forward manually by HR.
     */
    private InterviewStage currentStage;

    /**
     * When this candidate profile was created.
     */
    private LocalDateTime createdAt;

    /**
     * ID of the job description the candidate applied for.
     */
    private Long jobDescriptionId;

    /**
     * Title of the job the candidate applied for.
     * Included so frontend doesn't need a separate JD fetch.
     */
    private String jobTitle;

    /**
     * Default constructor needed for JSON serialization.
     */
    public CandidateResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
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

    public String getResumePath() {
        return resumePath;
    }

    public void setResumePath(final String resumePath) {
        this.resumePath = resumePath;
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

    public String getGender() {
        return gender;
    }

    public void setGender(final String gender) {
        this.gender = gender;
    }

    public InterviewStage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(final InterviewStage currentStage) {
        this.currentStage = currentStage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getJobDescriptionId() {
        return jobDescriptionId;
    }

    public void setJobDescriptionId(final Long jobDescriptionId) {
        this.jobDescriptionId = jobDescriptionId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(final String jobTitle) {
        this.jobTitle = jobTitle;
    }
}