package com.nucleusteq.interviewtracker.dto;

import com.nucleusteq.interviewtracker.enums.InterviewStage;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO for scheduling an interview by HR.
 * HR selects candidate, stage, date, time, panel members and focus areas.
 */
public class InterviewRequestDto {

    /**
     * ID of the candidate this interview is being scheduled for.
     * Cannot be null.
     */
    @NotNull(message = "Candidate ID is required")
    private Long candidateId;

    /**
     * Stage of this interview — L1_TECHNICAL, L2_TECHNICAL or HR_ROUND.
     * Cannot be null.
     */
    @NotNull(message = "Interview stage is required")
    private InterviewStage interviewStage;

    /**
     * Date of the interview.
     * Must be a future date — can't schedule in the past.
     */
    @NotNull(message = "Interview date is required")
    @Future(message = "Interview date must be in the future")
    private LocalDate interviewDate;

    /**
     * Time of the interview.
     * Cannot be null.
     */
    @NotNull(message = "Interview time is required")
    private LocalTime interviewTime;

    /**
     * Areas HR wants the panel to focus on during evaluation.
     * Free text — optional but recommended.
     */
    private String focusAreas;

    /**
     * Optional meeting link for online interviews (Meet, Zoom, etc).
     */
    private String meetingLink;

    /**
     * List of panel member IDs to assign to this interview.
     * Minimum 1 and maximum 2 panel members allowed per SRS.
     */
    @NotNull(message = "At least one panel member is required")
    @Size(min = 1, max = 2, message = "Interview must have between 1 and 2 panel members")
    private List<Long> panelMemberIds;

    /**
     * Default constructor needed for JSON deserialization.
     */
    public InterviewRequestDto() {
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(final Long candidateId) {
        this.candidateId = candidateId;
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

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(final String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public List<Long> getPanelMemberIds() {
        return panelMemberIds;
    }

    public void setPanelMemberIds(final List<Long> panelMemberIds) {
        this.panelMemberIds = panelMemberIds;
    }
}