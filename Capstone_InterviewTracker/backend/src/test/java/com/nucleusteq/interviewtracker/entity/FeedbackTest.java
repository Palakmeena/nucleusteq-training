package com.nucleusteq.interviewtracker.entity;

import com.nucleusteq.interviewtracker.enums.FeedbackStatus;
import com.nucleusteq.interviewtracker.enums.InterviewStage;
import com.nucleusteq.interviewtracker.enums.JobType;
import com.nucleusteq.interviewtracker.enums.UserRole;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Feedback entity.
 * Tests that panel feedback is created correctly
 * with all mandatory fields and getters setters work correctly.
 */
class FeedbackTest {

    /**
     * Helper method to create a sample PanelMember.
     */
    private PanelMember createSamplePanelMember() {
        return new PanelMember(
            "Rahul Verma",
            "rahul@gmail.com",
            "9876543210",
            "TechCorp",
            "Senior Developer"
        );
    }

    /**
     * Helper method to create a sample Candidate.
     */
    private Candidate createSampleCandidate() {
        User user = new User(
            "Palak Meena",
            "palak@gmail.com",
            "password123",
            UserRole.CANDIDATE
        );
        JobDescription jd = new JobDescription(
            "Backend Developer",
            "Java developer needed",
            2, 5, 6.0, 10.0,
            "Bangalore",
            JobType.FULL_TIME
        );
        return new Candidate(
            "Palak Meena",
            "palak@gmail.com",
            "+91",
            "9876543210",
            "NucleusTeq",
            1.0, 0.5,
            3.0, 6.0,
            30,
            "Bangalore",
            "LinkedIn",
            jd, user
        );
    }

    /**
     * Helper method to create a sample Interview.
     */
    private Interview createSampleInterview() {
        return new Interview(
            InterviewStage.L1_TECHNICAL,
            LocalDate.of(2026, 4, 25),
            LocalTime.of(11, 0),
            "Focus on Spring Boot and DSA",
            createSampleCandidate()
        );
    }

    /**
     * Helper method to create a sample Feedback.
     */
    private Feedback createSampleFeedback() {
        return new Feedback(
            "Candidate was confident and answered well",
            "Strong in Java and Spring Boot",
            "Needs to improve system design knowledge",
            "DSA, Spring Boot, REST APIs, SQL",
            4,
            FeedbackStatus.SELECTED,
            createSampleInterview(),
            createSamplePanelMember()
        );
    }

    @Test
    void shouldCreateFeedbackWithConstructor() {
        /** Act. */
        Feedback feedback = createSampleFeedback();

        /** Assert. */
        assertEquals("Candidate was confident and answered well",
                     feedback.getComments());
        assertEquals("Strong in Java and Spring Boot",
                     feedback.getStrengths());
        assertEquals("Needs to improve system design knowledge",
                     feedback.getWeaknesses());
        assertEquals("DSA, Spring Boot, REST APIs, SQL",
                     feedback.getAreasCovered());
        assertEquals(4, feedback.getRating());
        assertEquals(FeedbackStatus.SELECTED,
                     feedback.getFeedbackStatus());
    }

    @Test
    void shouldSetAndGetComments() {
        Feedback feedback = new Feedback();
        feedback.setComments("Good overall performance");
        assertEquals("Good overall performance",
                     feedback.getComments());
    }

    @Test
    void shouldSetAndGetStrengths() {
        Feedback feedback = new Feedback();
        feedback.setStrengths("Excellent problem solving");
        assertEquals("Excellent problem solving",
                     feedback.getStrengths());
    }

    @Test
    void shouldSetAndGetWeaknesses() {
        Feedback feedback = new Feedback();
        feedback.setWeaknesses("Needs to improve SQL skills");
        assertEquals("Needs to improve SQL skills",
                     feedback.getWeaknesses());
    }

    @Test
    void shouldSetAndGetAreasCovered() {
        Feedback feedback = new Feedback();
        feedback.setAreasCovered("Spring Boot, DSA, System Design");
        assertEquals("Spring Boot, DSA, System Design",
                     feedback.getAreasCovered());
    }

    @Test
    void shouldSetAndGetRating() {
        /** Rating should be between 1 and 5. */
        Feedback feedback = new Feedback();
        feedback.setRating(5);
        assertEquals(5, feedback.getRating());
    }

    @Test
    void shouldSetAndGetFeedbackStatus() {
        Feedback feedback = new Feedback();
        feedback.setFeedbackStatus(FeedbackStatus.REJECTED);
        assertEquals(FeedbackStatus.REJECTED,
                     feedback.getFeedbackStatus());
    }

    @Test
    void shouldSetAndGetInterview() {
        Feedback feedback = new Feedback();
        Interview interview = createSampleInterview();
        feedback.setInterview(interview);
        assertEquals(interview, feedback.getInterview());
    }

    @Test
    void shouldSetAndGetPanelMember() {
        Feedback feedback = new Feedback();
        PanelMember panelMember = createSamplePanelMember();
        feedback.setPanelMember(panelMember);
        assertEquals(panelMember, feedback.getPanelMember());
    }

    @Test
    void shouldHaveSubmittedAtSetOnConstruction() {
        /**
         * submittedAt should be automatically set
         * when feedback is created.
         */
        Feedback feedback = createSampleFeedback();
        assertNotNull(feedback.getSubmittedAt());
    }

    @Test
    void shouldAllowSelectedStatus() {
        /** Panel can recommend Selected. */
        Feedback feedback = createSampleFeedback();
        feedback.setFeedbackStatus(FeedbackStatus.SELECTED);
        assertEquals(FeedbackStatus.SELECTED,
                     feedback.getFeedbackStatus());
    }

    @Test
    void shouldAllowRejectedStatus() {
        /** Panel can recommend Rejected. */
        Feedback feedback = createSampleFeedback();
        feedback.setFeedbackStatus(FeedbackStatus.REJECTED);
        assertEquals(FeedbackStatus.REJECTED,
                     feedback.getFeedbackStatus());
    }

    @Test
    void shouldAllowTwoFeedbacksForSameInterview() {
        /**
         * If 2 panels assigned both submit separate feedback
         * for the same interview.
         */
        Interview interview = createSampleInterview();

        PanelMember panel1 = createSamplePanelMember();
        PanelMember panel2 = new PanelMember(
            "Sneha Joshi",
            "sneha@gmail.com",
            "8888888888",
            "InfoSys",
            "Tech Lead"
        );

        Feedback feedback1 = new Feedback(
            "Good performance",
            "Strong in Java",
            "Needs SQL work",
            "Java, Spring Boot",
            4,
            FeedbackStatus.SELECTED,
            interview,
            panel1
        );

        Feedback feedback2 = new Feedback(
            "Average performance",
            "Clear concepts",
            "Needs DSA work",
            "DSA, System Design",
            3,
            FeedbackStatus.SELECTED,
            interview,
            panel2
        );

        /** Both feedbacks linked to same interview. */
        assertEquals(interview, feedback1.getInterview());
        assertEquals(interview, feedback2.getInterview());

        /** But submitted by different panel members. */
        assertNotEquals(
            feedback1.getPanelMember().getEmail(),
            feedback2.getPanelMember().getEmail()
        );

        /** And can have different ratings. */
        assertNotEquals(feedback1.getRating(),
                        feedback2.getRating());
    }
}