package com.nucleusteq.interviewtracker.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Stores the candidate's "Live" profile information.
 * This is separate from the "Candidate" application record.
 * Changes here do not affect existing job applications (snapshots).
 */
@Entity
@Table(name = "candidate_profile")
public class CandidateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "mobile_code")
    private String mobileCode;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "resume_path")
    private String resumePath;

    @Column(name = "current_organization")
    private String currentOrganization;

    @Column(name = "total_experience")
    private Double totalExperience;

    @Column(name = "relevant_experience")
    private Double relevantExperience;

    @Column(name = "current_ctc")
    private Double currentCtc;

    @Column(name = "expected_ctc")
    private Double expectedCtc;

    @Column(name = "notice_period")
    private Integer noticePeriod;

    @Column(name = "preferred_location")
    private String preferredLocation;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public CandidateProfile() {
        this.updatedAt = LocalDateTime.now();
    }

    public CandidateProfile(User user) {
        this.user = user;
        this.fullName = user.getFullName();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getMobileCode() { return mobileCode; }
    public void setMobileCode(String mobileCode) { this.mobileCode = mobileCode; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getResumePath() { return resumePath; }
    public void setResumePath(String resumePath) { this.resumePath = resumePath; }
    public String getCurrentOrganization() { return currentOrganization; }
    public void setCurrentOrganization(String currentOrganization) { this.currentOrganization = currentOrganization; }
    public Double getTotalExperience() { return totalExperience; }
    public void setTotalExperience(Double totalExperience) { this.totalExperience = totalExperience; }
    public Double getRelevantExperience() { return relevantExperience; }
    public void setRelevantExperience(Double relevantExperience) { this.relevantExperience = relevantExperience; }
    public Double getCurrentCtc() { return currentCtc; }
    public void setCurrentCtc(Double currentCtc) { this.currentCtc = currentCtc; }
    public Double getExpectedCtc() { return expectedCtc; }
    public void setExpectedCtc(Double expectedCtc) { this.expectedCtc = expectedCtc; }
    public Integer getNoticePeriod() { return noticePeriod; }
    public void setNoticePeriod(Integer noticePeriod) { this.noticePeriod = noticePeriod; }
    public String getPreferredLocation() { return preferredLocation; }
    public void setPreferredLocation(String preferredLocation) { this.preferredLocation = preferredLocation; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
