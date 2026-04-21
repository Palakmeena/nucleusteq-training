package com.nucleusteq.interviewtracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

//  Represents the assignment of a panel member to an interview.

@Entity
@Table(name = "interview_panel")
public class InterviewPanel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panel_member_id", nullable = false)
    private PanelMember panelMember;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    // Default constructor required by JPA
    public InterviewPanel() {
    }

    // Constructor to create a new panel assignment
    public InterviewPanel(Interview interview, PanelMember panelMember) {
        this.interview = interview;
        this.panelMember = panelMember;
        this.assignedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}