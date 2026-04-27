package com.nucleusteq.interviewtracker.dto;

import com.nucleusteq.interviewtracker.enums.InterviewStage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for sending interview details back to the client.
 * Includes panel member names so frontend doesn't need extra calls.
 */
public class InterviewResponseDto {

    /**
     * Database ID of the interview.
     */
    private Long id;

    /**
     * ID of the candidate this interview belongs to.
     */
    private Long candidateId;

    /**
     * Full name of the candidate for easy display.
     */
    private String candidateName;

    /**
     * Stage of this interview — L1_TECHNICAL, L2_TECHNICAL or HR_ROUND.
     */
    private InterviewStage interviewStage;

    /**
     * Scheduled date of the interview.
     */
    private LocalDate interviewDate;

    /**
     * Scheduled time of the interview.
     */
    private LocalTime interviewTime;

    /**
     * Focus areas HR wants panel to evaluate.
     */
    private String focusAreas;

    /**
     * HR comments added after the interview round.
     */
    private String hrComments;

    /**
     * Whether this interview has been completed.
     */
    private boolean isCompleted;

    /**
     * When this interview was scheduled by HR.
     */
    private LocalDateTime createdAt;

    /**
     * Optional meeting link for online interviews.
     */
    private String meetingLink;

    /**
     * URL to view the candidate's resume (Google Drive Link).
     */
    private String resumeUrl;

    /**
     * ID of the job description for this interview.
     */
    private Long jdId;

    /**
     * Title of the job for display.
     */
    private String jdTitle;

    /**
     * Full job details (Description, Skills, etc).
     */
    private String jdDetails;

    /**
     * List of panel member names assigned to this interview.
     * Candidates can see names but not feedback per SRS.
     */
    private List<String> panelMemberNames;

    /**
     * List of panel member IDs — used by HR for management.
     */
    private List<Long> panelMemberIds;

    /**
     * Default constructor needed for JSON serialization.
     */
    public InterviewResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(final Long candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(final String candidateName) {
        this.candidateName = candidateName;
    }

    public InterviewStage getInterviewStage() {
        return interviewStage;
    }

    public void setInterviewStage(final InterviewStage interviewStage) {
        this.interviewStage = interviewStage;
    }

    public LocalDate getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(final LocalDate interviewDate) {
        this.interviewDate = interviewDate;
    }

    public LocalTime getInterviewTime() {
        return interviewTime;
    }

    public void setInterviewTime(final LocalTime interviewTime) {
        this.interviewTime = interviewTime;
    }

    public String getFocusAreas() {
        return focusAreas;
    }

    public void setFocusAreas(final String focusAreas) {
        this.focusAreas = focusAreas;
    }

    public String getHrComments() {
        return hrComments;
    }

    public void setHrComments(final String hrComments) {
        this.hrComments = hrComments;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(final boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(final String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(final String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public Long getJdId() {
        return jdId;
    }

    public void setJdId(final Long jdId) {
        this.jdId = jdId;
    }

    public String getJdTitle() {
        return jdTitle;
    }

    public void setJdTitle(final String jdTitle) {
        this.jdTitle = jdTitle;
    }

    public String getJdDetails() {
        return jdDetails;
    }

    public void setJdDetails(final String jdDetails) {
        this.jdDetails = jdDetails;
    }

    public List<String> getPanelMemberNames() {
        return panelMemberNames;
    }

    public void setPanelMemberNames(final List<String> panelMemberNames) {
        this.panelMemberNames = panelMemberNames;
    }

    public List<Long> getPanelMemberIds() {
        return panelMemberIds;
    }

    public void setPanelMemberIds(final List<Long> panelMemberIds) {
        this.panelMemberIds = panelMemberIds;
    }
}