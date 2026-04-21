package com.nucleusteq.interviewtracker.entity;

import com.nucleusteq.interviewtracker.enums.InterviewStage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Represents a candidate who has applied for a job.

@Entity
@Table(name = "candidate")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "mobile_code", nullable = false)
    private String mobileCode;

    @Column(name = "mobile_number", nullable = false, unique = true)
    private String mobileNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // Path where the candidate's resume PDF is stored on the server
    @Column(name = "resume_path")
    private String resumePath;

    @Column(name = "current_organization", nullable = false)
    private String currentOrganization;

    @Column(name = "total_experience", nullable = false)
    private Double totalExperience;

    @Column(name = "relevant_experience", nullable = false)
    private Double relevantExperience;

    @Column(name = "current_ctc", nullable = false)
    private Double currentCtc;

    @Column(name = "expected_ctc", nullable = false)
    private Double expectedCtc;

    @Column(name = "notice_period", nullable = false)
    private Integer noticePeriod;

    @Column(name = "preferred_location", nullable = false)
    private String preferredLocation;

    @Column(name = "source", nullable = false)
    private String source;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_stage", nullable = false)
    private InterviewStage currentStage = InterviewStage.PROFILING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jd_id", nullable = false)
    private JobDescription jobDescription;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Default constructor required by JPA
    public Candidate() {
    }

    // Constructor to create a new candidate profile
    public Candidate(String fullName, String email, String mobileCode,
            String mobileNumber, String currentOrganization,
            Double totalExperience, Double relevantExperience,
            Double currentCtc, Double expectedCtc,
            Integer noticePeriod, String preferredLocation,
            String source, JobDescription jobDescription, User user) {
        this.fullName = fullName;
        this.email = email;
        this.mobileCode = mobileCode;
        this.mobileNumber = mobileNumber;
        this.currentOrganization = currentOrganization;
        this.totalExperience = totalExperience;
        this.relevantExperience = relevantExperience;
        this.currentCtc = currentCtc;
        this.expectedCtc = expectedCtc;
        this.noticePeriod = noticePeriod;
        this.preferredLocation = preferredLocation;
        this.source = source;
        this.jobDescription = jobDescription;
        this.user = user;
        this.currentStage = InterviewStage.PROFILING;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileCode() {
        return mobileCode;
    }

    public void setMobileCode(String mobileCode) {
        this.mobileCode = mobileCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getResumePath() {
        return resumePath;
    }

    public void setResumePath(String resumePath) {
        this.resumePath = resumePath;
    }

    public String getCurrentOrganization() {
        return currentOrganization;
    }

    public void setCurrentOrganization(String currentOrganization) {
        this.currentOrganization = currentOrganization;
    }

    public Double getTotalExperience() {
        return totalExperience;
    }

    public void setTotalExperience(Double totalExperience) {
        this.totalExperience = totalExperience;
    }

    public Double getRelevantExperience() {
        return relevantExperience;
    }

    public void setRelevantExperience(Double relevantExperience) {
        this.relevantExperience = relevantExperience;
    }

    public Double getCurrentCtc() {
        return currentCtc;
    }

    public void setCurrentCtc(Double currentCtc) {
        this.currentCtc = currentCtc;
    }

    public Double getExpectedCtc() {
        return expectedCtc;
    }

    public void setExpectedCtc(Double expectedCtc) {
        this.expectedCtc = expectedCtc;
    }

    public Integer getNoticePeriod() {
        return noticePeriod;
    }

    public void setNoticePeriod(Integer noticePeriod) {
        this.noticePeriod = noticePeriod;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public InterviewStage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(InterviewStage currentStage) {
        this.currentStage = currentStage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public JobDescription getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(JobDescription jobDescription) {
        this.jobDescription = jobDescription;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}