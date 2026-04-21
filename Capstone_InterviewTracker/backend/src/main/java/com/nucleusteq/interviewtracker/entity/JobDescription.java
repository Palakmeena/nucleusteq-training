package com.nucleusteq.interviewtracker.entity;

import com.nucleusteq.interviewtracker.enums.JobType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Represents a Job Description posted by HR.

@Entity
@Table(name = "job_description")
public class JobDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @Column(name = "job_description", nullable = false, columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "min_experience", nullable = false)
    private Integer minExperience;

    @Column(name = "max_experience", nullable = false)
    private Integer maxExperience;

    @Column(name = "min_salary", nullable = false)
    private Double minSalary;

    @Column(name = "max_salary", nullable = false)
    private Double maxSalary;

    @Column(name = "location", nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private JobType jobType;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "jobDescription", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Skill> skills = new ArrayList<>();

    // Default constructor required by JPA
    public JobDescription() {
    }

    // Constructor to create a new JD with all required fields
    public JobDescription(String jobTitle, String jobDescription,
            Integer minExperience, Integer maxExperience,
            Double minSalary, Double maxSalary,
            String location, JobType jobType) {
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.minExperience = minExperience;
        this.maxExperience = maxExperience;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.location = location;
        this.jobType = jobType;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public Integer getMinExperience() {
        return minExperience;
    }

    public void setMinExperience(Integer minExperience) {
        this.minExperience = minExperience;
    }

    public Integer getMaxExperience() {
        return maxExperience;
    }

    public void setMaxExperience(Integer maxExperience) {
        this.maxExperience = maxExperience;
    }

    public Double getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Double minSalary) {
        this.minSalary = minSalary;
    }

    public Double getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Double maxSalary) {
        this.maxSalary = maxSalary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }
}