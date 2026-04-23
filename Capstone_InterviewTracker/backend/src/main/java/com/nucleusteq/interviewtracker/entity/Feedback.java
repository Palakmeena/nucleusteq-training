package com.nucleusteq.interviewtracker.entity;

import com.nucleusteq.interviewtracker.enums.FeedbackStatus;
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
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Represents the feedback submitted by a panel member
 * after conducting an L1 or L2 interview.
 * All fields are mandatory — panel cannot submit without filling everything.
 * Only HR can view this feedback — candidates cannot see it.
 */
@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comments", nullable = false, columnDefinition = "TEXT")
    private String comments;

    @Column(name = "strengths", nullable = false, columnDefinition = "TEXT")
    private String strengths;

    @Column(name = "weaknesses", nullable = false, columnDefinition = "TEXT")
    private String weaknesses;

    @Column(name = "areas_covered", nullable = false, columnDefinition = "TEXT")
    private String areasCovered;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    /** Panel member's recommendation — SELECTED or REJECTED.
     *  This is not the final decision, HR makes the final call. */
    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_status", nullable = false)
    private FeedbackStatus feedbackStatus;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panel_member_id", nullable = false)
    private PanelMember panelMember;

    /**
     * Default constructor required by JPA.
     */
    public Feedback() {
    }

    /**
     * Creates a new feedback record with all mandatory fields.
     */
    public Feedback(String comments, String strengths,
            String weaknesses, String areasCovered,
            Integer rating, FeedbackStatus feedbackStatus,
            Interview interview, PanelMember panelMember) {
        this.comments = comments;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.areasCovered = areasCovered;
        this.rating = rating;
        this.feedbackStatus = feedbackStatus;
        this.interview = interview;
        this.panelMember = panelMember;
        this.submittedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getAreasCovered() {
        return areasCovered;
    }

    public void setAreasCovered(String areasCovered) {
        this.areasCovered = areasCovered;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public FeedbackStatus getFeedbackStatus() {
        return feedbackStatus;
    }

    public void setFeedbackStatus(FeedbackStatus feedbackStatus) {
        this.feedbackStatus = feedbackStatus;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public PanelMember getPanelMember() {
        return panelMember;
    }

    public void setPanelMember(PanelMember panelMember) {
        this.panelMember = panelMember;
    }
}