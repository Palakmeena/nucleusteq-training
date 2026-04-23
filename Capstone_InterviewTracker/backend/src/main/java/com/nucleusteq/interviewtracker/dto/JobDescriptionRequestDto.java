package com.nucleusteq.interviewtracker.dto;

import com.nucleusteq.interviewtracker.enums.JobType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO for creating or updating a Job Description.
 * This is what HR sends in the request body when posting a new job.
 */
public class JobDescriptionRequestDto {

    /**
     * Title of the job posting e.g. Backend Developer, QA Engineer.
     */
    @NotBlank(message = "Job title is required")
    @Size(max = 100, message = "Job title cannot exceed 100 characters")
    private String jobTitle;

    /**
     * Detailed description of the role and responsibilities.
     */
    @NotBlank(message = "Job description is required")
    private String jobDescription;

    /**
     * Minimum years of experience required.
     */
    @NotNull(message = "Minimum experience is required")
    @Min(value = 0, message = "Minimum experience cannot be negative")
    private Integer minExperience;

    /**
     * Maximum years of experience required.
     */
    @NotNull(message = "Maximum experience is required")
    @Min(value = 1, message = "Maximum experience must be at least 1 year")
    private Integer maxExperience;

    /**
     * Minimum salary offered in LPA (Lakhs Per Annum).
     */
    @NotNull(message = "Minimum salary is required")
    @DecimalMin(value = "0.1", message = "Minimum salary must be greater than 0")
    private Double minSalary;

    /**
     * Maximum salary offered in LPA.
     */
    @NotNull(message = "Maximum salary is required")
    @DecimalMin(value = "0.1", message = "Maximum salary must be greater than 0")
    private Double maxSalary;

    /**
     * Location where the job is based e.g. Bangalore, Hyderabad, Remote.
     */
    @NotBlank(message = "Location is required")
    private String location;

    /**
     * Type of employment — FULL_TIME, CONTRACT, or REMOTE.
     */
    @NotNull(message = "Job type is required")
    private JobType jobType;

    /**
     * List of skills required for this job e.g. ["Java", "Spring Boot", "MySQL"].
     */
    @NotEmpty(message = "At least one skill is required")
    private List<String> skills;

    /**
     * Default constructor needed for JSON deserialization.
     */
    public JobDescriptionRequestDto() {
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

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(final List<String> skills) {
        this.skills = skills;
    }
}