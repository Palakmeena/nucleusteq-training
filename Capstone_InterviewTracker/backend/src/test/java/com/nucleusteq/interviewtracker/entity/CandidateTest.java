package com.nucleusteq.interviewtracker.entity;

import com.nucleusteq.interviewtracker.enums.InterviewStage;
import com.nucleusteq.interviewtracker.enums.JobType;
import com.nucleusteq.interviewtracker.enums.UserRole;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Candidate entity.
 * Tests candidate profile creation, default stage,
 * getters and setters work correctly.
 */
class CandidateTest {

    /**
     * Helper method to create a sample User
     * so we don't repeat this code in every test.
     */
    private User createSampleUser() {
        return new User(
            "Palak Meena",
            "palak@gmail.com",
            "password123",
            UserRole.CANDIDATE
        );
    }

    /**
     * Helper method to create a sample JobDescription.
     */
    private JobDescription createSampleJobDescription() {
        return new JobDescription(
            "Backend Developer",
            "Java developer needed",
            2, 5, 6.0, 10.0,
            "Bangalore",
            JobType.FULL_TIME
        );
    }

    /**
     * Helper method to create a sample Candidate
     * so we don't repeat this code in every test.
     */
    private Candidate createSampleCandidate() {
        return new Candidate(
            "+91",
            "9876543210",
            "NucleusTeq",
            1.0,
            0.5,
            3.0,
            6.0,
            30,
            "Bangalore",
            "LinkedIn",
            createSampleJobDescription(),
            createSampleUser()
        );
    }

    @Test
    void shouldCreateCandidateWithConstructor() {
        /** Act. */
        Candidate candidate = createSampleCandidate();

        /** Assert. */
        assertEquals("Palak Meena", candidate.getFullName());
        assertEquals("palak@gmail.com", candidate.getEmail());
        assertEquals("+91", candidate.getMobileCode());
        assertEquals("9876543210", candidate.getMobileNumber());
        assertEquals("NucleusTeq", candidate.getCurrentOrganization());
        assertEquals(1.0, candidate.getTotalExperience());
        assertEquals(0.5, candidate.getRelevantExperience());
        assertEquals(3.0, candidate.getCurrentCtc());
        assertEquals(6.0, candidate.getExpectedCtc());
        assertEquals(30, candidate.getNoticePeriod());
        assertEquals("Bangalore", candidate.getPreferredLocation());
        assertEquals("LinkedIn", candidate.getSource());
    }

    @Test
    void shouldHaveProfilingAsDefaultStage() {
        /**
         * When candidate is created their stage should
         * automatically be set to PROFILING.
         */
        Candidate candidate = createSampleCandidate();
        assertEquals(InterviewStage.PROFILING, candidate.getCurrentStage());
    }

    @Test
    void shouldSetAndGetCurrentStage() {
        /** HR moves candidate to next stage. */
        Candidate candidate = createSampleCandidate();
        candidate.setCurrentStage(InterviewStage.SCREENING);
        assertEquals(InterviewStage.SCREENING, candidate.getCurrentStage());
    }

    @Test
    void shouldSetAndGetFullName() {
        Candidate candidate = new Candidate();
        User user = new User("Rahul Sharma", "rahul@test.com", "pass", UserRole.CANDIDATE);
        candidate.setUser(user);
        assertEquals("Rahul Sharma", candidate.getFullName());
    }

    @Test
    void shouldSetAndGetEmail() {
        Candidate candidate = new Candidate();
        User user = new User("Rahul Sharma", "rahul@gmail.com", "pass", UserRole.CANDIDATE);
        candidate.setUser(user);
        assertEquals("rahul@gmail.com", candidate.getEmail());
    }

    @Test
    void shouldSetAndGetResumePath() {
        /** Resume path is where PDF is stored on server. */
        Candidate candidate = new Candidate();
        candidate.setResumePath("/uploads/resumes/palak_resume.pdf");
        assertEquals("/uploads/resumes/palak_resume.pdf",
                     candidate.getResumePath());
    }

    @Test
    void shouldSetAndGetJobDescription() {
        /** Candidate should be linked to a JD. */
        Candidate candidate = new Candidate();
        JobDescription jd = createSampleJobDescription();
        candidate.setJobDescription(jd);
        assertEquals(jd, candidate.getJobDescription());
    }

    @Test
    void shouldSetAndGetUser() {
        /** Candidate should be linked to a User account. */
        Candidate candidate = new Candidate();
        User user = createSampleUser();
        candidate.setUser(user);
        assertEquals(user, candidate.getUser());
    }

    @Test
    void shouldAllowStageProgressionToRejected() {
        /** Candidate can be rejected at any stage. */
        Candidate candidate = createSampleCandidate();
        candidate.setCurrentStage(InterviewStage.REJECTED);
        assertEquals(InterviewStage.REJECTED, candidate.getCurrentStage());
    }

    @Test
    void shouldAllowNullDateOfBirth() {
        /** Date of birth is optional as per spec. */
        Candidate candidate = createSampleCandidate();
        assertNull(candidate.getDateOfBirth());
    }
}