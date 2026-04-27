package com.nucleusteq.interviewtracker.dto;

import java.time.LocalDateTime;

/**
 * DTO for sending feedback details to HR.
 */
public class FeedbackResponseDto {
    private String panelMemberName;
    private int rating;
    private String comments;
    private String strengths;
    private String weaknesses;
    private String decision;
    private LocalDateTime submittedAt;

    public FeedbackResponseDto() {}

    // Getters and Setters
    public String getPanelMemberName() { return panelMemberName; }
    public void setPanelMemberName(String panelMemberName) { this.panelMemberName = panelMemberName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }

    public String getWeaknesses() { return weaknesses; }
    public void setWeaknesses(String weaknesses) { this.weaknesses = weaknesses; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
