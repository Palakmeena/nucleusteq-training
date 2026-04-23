package com.nucleusteq.interviewtracker.dto;

import com.nucleusteq.interviewtracker.enums.JobType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for sending Job Description data back to the client.
 */
public class JobDescriptionResponseDto {

    /**
     * Database ID of the job description.
     */
    private Long id;

    /** Title of the job posting. */
    private String jobTitle;

    /** Full description of the role and responsibilities. */
    private String jobDescription;

    /** Minimum years of experience required. */
    private Integer minExperience;

    /** Maximum years of experience required. */
    private Integer maxExperience;

    /** Minimum salary offered in LPA. */
    private Double minSalary;

    /** Maximum salary offered in LPA. */
    private Double maxSalary;

    /** Location where the job is based. */
    private String location;

    /** Type of employment — FULL_TIME, CONTRACT, or REMOTE. */
    private JobType jobType;

    /**
     * Whether this job posting is currently accepting applications.
     */
    private boolean isActive;

    /**
     * When this JD was created.
     */
    private LocalDateTime createdAt;

    /**
     * List of skill names required for this job.
     */
    private List<String> skills;

    /**
     * Default constructor needed for JSON serialization.
     */
    public JobDescriptionResponseDto() {
    }

    /**
     * All-args constructor — used in the mapper to build the response
     * from a JobDescription entity in one clean call.
     */
    public JobDescriptionResponseDto(final Long id,
                                     final String jobTitle,
                                     final String jobDescription,
                                     final Integer minExperience,
                                     final Integer maxExperience,
                                     final Double minSalary,
                                     final Double maxSalary,
                                     final String location,
                                     final JobType jobType,
                                     final boolean isActive,
                                     final LocalDateTime createdAt,
                                     final List<String> skills) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.minExperience = minExperience;
        this.maxExperience = maxExperience;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.location = location;
        this.jobType = jobType;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.skills = skills;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(final String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(final String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public Integer getMinExperience() {
        return minExperience;
    }

    public void setMinExperience(final Integer minExperience) {
        this.minExperience = minExperience;
    }

    public Integer getMaxExperience() {
        return maxExperience;
    }

    public void setMaxExperience(final Integer maxExperience) {
        this.maxExperience = maxExperience;
    }

    public Double getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(final Double minSalary) {
        this.minSalary = minSalary;
    }

    public Double getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(final Double maxSalary) {
        this.maxSalary = maxSalary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(final JobType jobType) {
        this.jobType = jobType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(final boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(final List<String> skills) {
        this.skills = skills;
    }
}