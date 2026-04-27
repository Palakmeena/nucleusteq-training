package com.nucleusteq.interviewtracker.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for panel members to submit interview evaluation.
 */
public class FeedbackRequestDto {

    @Min(1) @Max(5)
    private int rating;

    @NotBlank(message = "Comments are mandatory")
    private String comments;

    private String strengths;
    private String weaknesses;
    private String decision;

    public FeedbackRequestDto() {}

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
}
