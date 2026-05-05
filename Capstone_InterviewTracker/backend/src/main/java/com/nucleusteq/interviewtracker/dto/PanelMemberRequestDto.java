package com.nucleusteq.interviewtracker.dto;

import com.nucleusteq.interviewtracker.util.AppConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PanelMemberRequestDto {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @NotBlank(message = AppConstants.EMAIL_REQUIRED)
    @Email(message = AppConstants.INVALID_EMAIL)
    private String email;

    /**
     * Mobile number of the panel member.
     * Must be unique across all panel members.
     */
    @NotBlank(message = "Mobile number is required")
    @Size(min = 10, max = 15, message = "Mobile number must be between 10 and 15 digits")
    private String mobileNumber;

    /**
     * Organization the panel member works for.
     * Cannot be blank.
     */
    @NotBlank(message = "Organization is required")
    private String organization;

    /**
     * Job designation of the panel member e.g. Senior Developer.
     * Cannot be blank.
     */
    @NotBlank(message = "Designation is required")
    private String designation;

    /**
     * Default constructor needed for JSON deserialization.
     */
    public PanelMemberRequestDto() {
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
}