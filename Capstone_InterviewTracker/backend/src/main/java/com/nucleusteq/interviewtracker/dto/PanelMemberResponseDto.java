package com.nucleusteq.interviewtracker.dto;

import java.time.LocalDateTime;

/**
 * DTO for sending panel member data back to the client.
 * Never exposes sensitive fields like password or activation token.
 */
public class PanelMemberResponseDto {

    /**
     * Database ID of the panel member.
     * HR needs this to assign panel members to interviews.
     */
    private Long id;

    /**
     * Full name of the panel member.
     */
    private String fullName;

    /**
     * Email address of the panel member.
     */
    private String email;

    /**
     * Mobile number of the panel member.
     */
    private String mobileNumber;

    /**
     * Organization the panel member works for.
     */
    private String organization;

    /**
     * Job designation of the panel member.
     */
    private String designation;

    /**
     * Whether the panel member has activated their account.
     * HR uses this to know if the panel member can log in yet.
     */
    private boolean isActive;

    /**
     * When HR created this panel member account.
     */
    private LocalDateTime createdAt;

    /**
     * One-time activation link generated for this panel account.
     * Returned to HR as a fallback when email delivery fails.
     */
    private String activationLink;

    /**
     * True when activation email was successfully sent.
     */
    private boolean activationEmailSent;

    /**
     * Default constructor needed for JSON serialization.
     */
    public PanelMemberResponseDto() {
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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(final String organization) {
        this.organization = organization;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(final String designation) {
        this.designation = designation;
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

    public String getActivationLink() {
        return activationLink;
    }

    public void setActivationLink(final String activationLink) {
        this.activationLink = activationLink;
    }

    public boolean isActivationEmailSent() {
        return activationEmailSent;
    }

    public void setActivationEmailSent(final boolean activationEmailSent) {
        this.activationEmailSent = activationEmailSent;
    }
}