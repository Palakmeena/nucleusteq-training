package com.nucleusteq.interviewtracker.mapper;

import com.nucleusteq.interviewtracker.dto.InterviewResponseDto;
import com.nucleusteq.interviewtracker.entity.Candidate;
import com.nucleusteq.interviewtracker.entity.Feedback;
import com.nucleusteq.interviewtracker.entity.Interview;
import com.nucleusteq.interviewtracker.entity.InterviewPanel;
import com.nucleusteq.interviewtracker.entity.JobDescription;
import com.nucleusteq.interviewtracker.entity.PanelMember;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.enums.FeedbackStatus;
import com.nucleusteq.interviewtracker.enums.InterviewStage;
import com.nucleusteq.interviewtracker.enums.JobType;
import com.nucleusteq.interviewtracker.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InterviewMapperTest {

    private InterviewMapper mapper;
    private Interview interview;
    private Candidate candidate;
    private User candidateUser;
    private JobDescription jd;
    private List<InterviewPanel> interviewPanels;
    private List<Feedback> feedbacks;

    @BeforeEach
    void setUp() {
        mapper = new InterviewMapper();

        // Setup JobDescription
        jd = new JobDescription();
        jd.setId(1L);
        jd.setJobTitle("Senior Java Developer");
        jd.setJobDescription("Backend development role");
        jd.setJobType(JobType.FULL_TIME);

        // Setup Candidate User
        candidateUser = new User();
        candidateUser.setEmail("candidate@example.com");
        candidateUser.setRole(UserRole.CANDIDATE);

        // Setup Candidate
        candidate = new Candidate();
        candidate.setId(1L);
        candidateUser.setFullName("John Doe");
        candidateUser.setEmail("candidate@example.com");
        candidate.setJobDescription(jd);
        candidate.setUser(candidateUser);
        candidate.setResumePath("/resumes/john-resume.pdf");

        // Setup Interview
        interview = new Interview();
        interview.setId(1L);
        interview.setCandidate(candidate);
        interview.setInterviewStage(InterviewStage.L1_TECHNICAL);
        interview.setInterviewDate(LocalDate.of(2024, 5, 15));
        interview.setInterviewTime(LocalTime.of(14, 0));
        interview.setFocusAreas("Core Java, Microservices");
        interview.setHrComments("Good candidate");
        interview.setCompleted(false);
        interview.setCreatedAt(LocalDateTime.now());
        interview.setMeetingLink("https://meet.example.com/interview123");

        // Setup Interview Panels with Panel Members
        interviewPanels = new ArrayList<>();
        
        User panelUser1 = new User();
        panelUser1.setEmail("panel1@example.com");
        panelUser1.setRole(UserRole.PANEL);
        
        PanelMember panelMember1 = new PanelMember();
        panelMember1.setId(10L);
        panelUser1.setFullName("Alice Smith");
        panelMember1.setUser(panelUser1);

        InterviewPanel panel1 = new InterviewPanel();
        panel1.setInterview(interview);
        panel1.setPanelMember(panelMember1);
        interviewPanels.add(panel1);

        User panelUser2 = new User();
        panelUser2.setEmail("panel2@example.com");
        panelUser2.setRole(UserRole.PANEL);
        
        PanelMember panelMember2 = new PanelMember();
        panelMember2.setId(11L);
        panelUser2.setFullName("Bob Johnson");
        panelMember2.setUser(panelUser2);

        InterviewPanel panel2 = new InterviewPanel();
        panel2.setInterview(interview);
        panel2.setPanelMember(panelMember2);
        interviewPanels.add(panel2);

        interview.setInterviewPanels(interviewPanels);

        // Setup Feedbacks
        feedbacks = new ArrayList<>();
        
        Feedback feedback1 = new Feedback();
        feedback1.setId(1L);
        feedback1.setPanelMember(panelMember1);
        feedback1.setRating(4);
        feedback1.setComments("Strong technical skills");
        feedback1.setStrengths("Problem solving, communication");
        feedback1.setWeaknesses("Limited microservices experience");
        feedback1.setAreasCovered("Collections, Streams, Design Patterns");
        feedback1.setFeedbackStatus(FeedbackStatus.SELECTED);
        feedback1.setPanelSuggestion("Move to next round");
        feedback1.setSubmittedAt(LocalDateTime.now());
        feedbacks.add(feedback1);

        Feedback feedback2 = new Feedback();
        feedback2.setId(2L);
        feedback2.setPanelMember(panelMember2);
        feedback2.setRating(3);
        feedback2.setComments("Average performance");
        feedback2.setStrengths("Basic coding");
        feedback2.setWeaknesses("Design patterns, system design");
        feedback2.setAreasCovered("Java basics");
        feedback2.setFeedbackStatus(FeedbackStatus.REJECTED);
        feedback2.setPanelSuggestion("Conduct follow-up test");
        feedback2.setSubmittedAt(LocalDateTime.now());
        feedbacks.add(feedback2);

        interview.setFeedbacks(feedbacks);
    }

    @Test
    void mapToResponseDto_shouldMapBasicInterviewFields() {
        InterviewResponseDto result = mapper.mapToResponseDto(interview);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getCandidateId());
        assertEquals("John Doe", result.getCandidateName());
        assertEquals(InterviewStage.L1_TECHNICAL, result.getInterviewStage());
        assertEquals(LocalDate.of(2024, 5, 15), result.getInterviewDate());
        assertEquals(LocalTime.of(14, 0), result.getInterviewTime());
        assertEquals("Core Java, Microservices", result.getFocusAreas());
        assertEquals("Good candidate", result.getHrComments());
        assertFalse(result.isCompleted());
    }

    @Test
    void mapToResponseDto_shouldMapPanelMembers() {
        InterviewResponseDto result = mapper.mapToResponseDto(interview);

        assertNotNull(result.getPanelMemberNames());
        assertNotNull(result.getPanelMemberIds());
        
        assertEquals(2, result.getPanelMemberNames().size());
        assertEquals(2, result.getPanelMemberIds().size());
        
        assertTrue(result.getPanelMemberNames().contains("Alice Smith"));
        assertTrue(result.getPanelMemberNames().contains("Bob Johnson"));
        assertTrue(result.getPanelMemberIds().contains(10L));
        assertTrue(result.getPanelMemberIds().contains(11L));
    }

    @Test
    void mapToResponseDto_shouldMapMeetingLinkAndResume() {
        InterviewResponseDto result = mapper.mapToResponseDto(interview);

        assertEquals("https://meet.example.com/interview123", result.getMeetingLink());
        assertEquals("/resumes/john-resume.pdf", result.getResumeUrl());
    }

    @Test
    void mapToResponseDto_shouldMapJobDescriptionDetails() {
        InterviewResponseDto result = mapper.mapToResponseDto(interview);

        assertEquals(1L, result.getJdId());
        assertEquals("Senior Java Developer", result.getJdTitle());
        assertEquals("Backend development role", result.getJdDetails());
    }

    @Test
    void mapToResponseDto_shouldMapFeedbacks() {
        InterviewResponseDto result = mapper.mapToResponseDto(interview);

        assertNotNull(result.getFeedbacks());
        assertEquals(2, result.getFeedbacks().size());
        
        var feedback1 = result.getFeedbacks().get(0);
        assertEquals("Alice Smith", feedback1.getPanelMemberName());
        assertEquals(4, feedback1.getRating());
        assertEquals("Strong technical skills", feedback1.getComments());
        assertEquals("Problem solving, communication", feedback1.getStrengths());
        assertEquals("Limited microservices experience", feedback1.getWeaknesses());
        assertEquals("SELECTED", feedback1.getDecision());
        assertEquals("Move to next round", feedback1.getPanelSuggestion());
        assertNotNull(feedback1.getSubmittedAt());
    }

    @Test
    void mapToResponseDto_shouldHandleNullJobDescription() {
        candidate.setJobDescription(null);

        InterviewResponseDto result = mapper.mapToResponseDto(interview);

        assertNotNull(result);
        assertNull(result.getJdId());
        assertNull(result.getJdTitle());
        assertNull(result.getJdDetails());
    }

    @Test
    void mapToResponseDto_shouldHandleNullFeedbacks() {
        interview.setFeedbacks(null);

        InterviewResponseDto result = mapper.mapToResponseDto(interview);

        assertNotNull(result);
        assertNull(result.getFeedbacks());
    }

    @Test
    void mapToResponseDto_shouldHandleEmptyFeedbacks() {
        interview.setFeedbacks(new ArrayList<>());

        InterviewResponseDto result = mapper.mapToResponseDto(interview);

        assertNotNull(result.getFeedbacks());
        assertEquals(0, result.getFeedbacks().size());
    }

    @Test
    void mapToResponseDto_shouldHandleEmptyPanelMembers() {
        interview.setInterviewPanels(new ArrayList<>());

        InterviewResponseDto result = mapper.mapToResponseDto(interview);

        assertNotNull(result.getPanelMemberNames());
        assertNotNull(result.getPanelMemberIds());
        assertEquals(0, result.getPanelMemberNames().size());
        assertEquals(0, result.getPanelMemberIds().size());
    }

    @Test
    void mapToResponseDto_shouldMapFeedbackWithHRAdmin() {
        // Feedback without panel member (HR admin feedback)
        Feedback hrFeedback = new Feedback();
        hrFeedback.setId(3L);
        hrFeedback.setPanelMember(null);
        hrFeedback.setRating(5);
        hrFeedback.setComments("Excellent overall");
        hrFeedback.setFeedbackStatus(FeedbackStatus.SELECTED);
        hrFeedback.setSubmittedAt(LocalDateTime.now());
        
        feedbacks.add(hrFeedback);

        InterviewResponseDto result = mapper.mapToResponseDto(interview);

        assertEquals(3, result.getFeedbacks().size());
        var lastFeedback = result.getFeedbacks().get(2);
        assertEquals("HR Admin", lastFeedback.getPanelMemberName());
    }

    @Test
    void mapToResponseDto_shouldPreserveCreatedAt() {
        LocalDateTime createdAt = interview.getCreatedAt();

        InterviewResponseDto result = mapper.mapToResponseDto(interview);

        assertEquals(createdAt, result.getCreatedAt());
    }
}
