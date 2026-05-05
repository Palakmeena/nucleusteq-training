package com.nucleusteq.interviewtracker.entity;

import com.nucleusteq.interviewtracker.enums.InterviewStage;
import com.nucleusteq.interviewtracker.enums.JobType;
import com.nucleusteq.interviewtracker.enums.UserRole;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Interview entity.
 * Tests interview creation, default values,
 * and getters and setters work correctly.
 */
class InterviewTest {

    /**
     * Helper method to create a sample Candidate
     * so we don't repeat this code in every test.
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
            "Focus on Spring Boot, DSA and System Design",
            createSampleCandidate()
        );
    }

    @Test
    void shouldCreateInterviewWithConstructor() {
        /** Act. */
        Interview interview = createSampleInterview();

        /** Assert. */
        assertEquals(InterviewStage.L1_TECHNICAL,
                     interview.getInterviewStage());
        assertEquals(LocalDate.of(2026, 4, 25),
                     interview.getInterviewDate());
        assertEquals(LocalTime.of(11, 0),
                     interview.getInterviewTime());
        assertEquals("Focus on Spring Boot, DSA and System Design",
                     interview.getFocusAreas());
        assertNotNull(interview.getCandidate());
    }

    @Test
    void shouldHaveFalseAsDefaultCompletedStatus() {
        /**
         * Interview starts as not completed.
         * It becomes completed after panel submits feedback.
         */
        Interview interview = createSampleInterview();
        assertFalse(interview.isCompleted());
    }

    @Test
    void shouldMarkInterviewAsCompleted() {
        Interview interview = createSampleInterview();
        interview.setCompleted(true);
        assertTrue(interview.isCompleted());
    }

    @Test
    void shouldSetAndGetInterviewStage() {
        Interview interview = new Interview();
        interview.setInterviewStage(InterviewStage.L2_TECHNICAL);
        assertEquals(InterviewStage.L2_TECHNICAL,
                     interview.getInterviewStage());
    }

    @Test
    void shouldSetAndGetInterviewDate() {
        Interview interview = new Interview();
        LocalDate date = LocalDate.of(2026, 5, 1);
        interview.setInterviewDate(date);
        assertEquals(date, interview.getInterviewDate());
    }

    @Test
    void shouldSetAndGetInterviewTime() {
        Interview interview = new Interview();
        LocalTime time = LocalTime.of(14, 30);
        interview.setInterviewTime(time);
        assertEquals(time, interview.getInterviewTime());
    }

    @Test
    void shouldSetAndGetFocusAreas() {
        Interview interview = new Interview();
        interview.setFocusAreas("Focus on DSA and SQL");
        assertEquals("Focus on DSA and SQL", interview.getFocusAreas());
    }

    @Test
    void shouldSetAndGetHrComments() {
        /** HR comments are filled during HR Round final decision. */
        Interview interview = new Interview();
        interview.setHrComments("Candidate is a great fit for the role");
        assertEquals("Candidate is a great fit for the role",
                     interview.getHrComments());
    }

    @Test
    void shouldHaveNullHrCommentsByDefault() {
        /**
         * HR comments should be null initially.
         * Only filled during HR Round.
         */
        Interview interview = createSampleInterview();
        assertNull(interview.getHrComments());
    }

    @Test
    void shouldHaveEmptyInterviewPanelsListByDefault() {
        /** No panels assigned initially. */
        Interview interview = new Interview();
        assertNotNull(interview.getInterviewPanels());
        assertEquals(0, interview.getInterviewPanels().size());
    }

    @Test
    void shouldHaveEmptyFeedbacksListByDefault() {
        /** No feedbacks initially. */
        Interview interview = new Interview();
        assertNotNull(interview.getFeedbacks());
        assertEquals(0, interview.getFeedbacks().size());
    }

    @Test
    void shouldSetAndGetCandidate() {
        Interview interview = new Interview();
        Candidate candidate = createSampleCandidate();
        interview.setCandidate(candidate);
        assertEquals(candidate, interview.getCandidate());
    }

    @Test
    void shouldHaveCreatedAtSetOnConstruction() {
        /**
         * createdAt should be set automatically
         * when interview is created.
         */
        Interview interview = createSampleInterview();
        assertNotNull(interview.getCreatedAt());
    }
}