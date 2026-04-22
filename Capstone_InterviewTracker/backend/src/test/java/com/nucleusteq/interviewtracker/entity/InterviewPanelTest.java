package com.nucleusteq.interviewtracker.entity;

import com.nucleusteq.interviewtracker.enums.InterviewStage;
import com.nucleusteq.interviewtracker.enums.JobType;
import com.nucleusteq.interviewtracker.enums.UserRole;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InterviewPanel entity.
 * Tests that panel members are correctly mapped
 * to interviews and assignment details are saved correctly.
 */
class InterviewPanelTest {

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

    @Test
    void shouldCreateInterviewPanelWithConstructor() {
        /** Arrange. */
        Interview interview = createSampleInterview();
        PanelMember panelMember = createSamplePanelMember();

        /** Act. */
        InterviewPanel interviewPanel = new InterviewPanel(
            interview, panelMember
        );

        /** Assert. */
        assertEquals(interview, interviewPanel.getInterview());
        assertEquals(panelMember, interviewPanel.getPanelMember());
    }

    @Test
    void shouldSetAndGetInterview() {
        /** Test setter and getter work correctly. */
        InterviewPanel interviewPanel = new InterviewPanel();
        Interview interview = createSampleInterview();
        interviewPanel.setInterview(interview);
        assertEquals(interview, interviewPanel.getInterview());
    }

    @Test
    void shouldSetAndGetPanelMember() {
        /** Test setter and getter work correctly. */
        InterviewPanel interviewPanel = new InterviewPanel();
        PanelMember panelMember = createSamplePanelMember();
        interviewPanel.setPanelMember(panelMember);
        assertEquals(panelMember, interviewPanel.getPanelMember());
    }

    @Test
    void shouldSetAndGetId() {
        InterviewPanel interviewPanel = new InterviewPanel();
        interviewPanel.setId(1L);
        assertEquals(1L, interviewPanel.getId());
    }

    @Test
    void shouldHaveAssignedAtSetOnConstruction() {
        /**
         * assignedAt should be automatically set
         * when HR assigns a panel member to an interview.
         */
        Interview interview = createSampleInterview();
        PanelMember panelMember = createSamplePanelMember();
        InterviewPanel interviewPanel = new InterviewPanel(
            interview, panelMember
        );
        assertNotNull(interviewPanel.getAssignedAt());
    }

    @Test
    void shouldAllowTwoPanelMembersForSameInterview() {
        /**
         * One interview can have maximum 2 panel members.
         * This tests that two different assignments
         * can exist for the same interview.
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

        InterviewPanel assignment1 = new InterviewPanel(
            interview, panel1
        );
        InterviewPanel assignment2 = new InterviewPanel(
            interview, panel2
        );

        /** Both assignments should point to same interview. */
        assertEquals(interview, assignment1.getInterview());
        assertEquals(interview, assignment2.getInterview());

        /** But different panel members. */
        assertNotEquals(
            assignment1.getPanelMember().getEmail(),
            assignment2.getPanelMember().getEmail()
        );
    }
}