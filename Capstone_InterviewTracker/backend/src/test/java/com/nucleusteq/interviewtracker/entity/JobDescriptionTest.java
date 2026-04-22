package com.nucleusteq.interviewtracker.entity;

import com.nucleusteq.interviewtracker.enums.JobType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JobDescription entity.
 * Tests object creation, default values,
 * and getters and setters work correctly.
 */
class JobDescriptionTest {

    @Test
    void shouldCreateJobDescriptionWithConstructor() {
        /** Arrange. */
        String jobTitle = "Backend Developer";
        String jobDescription = "We need a Java developer";
        Integer minExp = 2;
        Integer maxExp = 5;
        Double minSalary = 6.0;
        Double maxSalary = 10.0;
        String location = "Bangalore";
        JobType jobType = JobType.FULL_TIME;

        /** Act. */
        JobDescription jd = new JobDescription(
            jobTitle, jobDescription, minExp, maxExp,
            minSalary, maxSalary, location, jobType
        );

        /** Assert. */
        assertEquals(jobTitle, jd.getJobTitle());
        assertEquals(jobDescription, jd.getJobDescription());
        assertEquals(minExp, jd.getMinExperience());
        assertEquals(maxExp, jd.getMaxExperience());
        assertEquals(minSalary, jd.getMinSalary());
        assertEquals(maxSalary, jd.getMaxSalary());
        assertEquals(location, jd.getLocation());
        assertEquals(jobType, jd.getJobType());
    }

    @Test
    void shouldBeTrueAsDefaultActiveStatus() {
        /** New JD should always be active by default. */
        JobDescription jd = new JobDescription(
            "Backend Developer", "Java developer needed",
            2, 5, 6.0, 10.0, "Bangalore", JobType.FULL_TIME
        );
        assertTrue(jd.isActive());
    }

    @Test
    void shouldSetAndGetJobTitle() {
        /** Test setter and getter work correctly. */
        JobDescription jd = new JobDescription();
        jd.setJobTitle("Frontend Developer");
        assertEquals("Frontend Developer", jd.getJobTitle());
    }

    @Test
    void shouldSetAndGetJobType() {
        JobDescription jd = new JobDescription();
        jd.setJobType(JobType.REMOTE);
        assertEquals(JobType.REMOTE, jd.getJobType());
    }

    @Test
    void shouldSetActiveStatusCorrectly() {
        JobDescription jd = new JobDescription();
        jd.setActive(false);
        assertFalse(jd.isActive());
    }

    @Test
    void shouldHaveEmptySkillsListByDefault() {
        /** Skills list should be empty when JD is first created. */
        JobDescription jd = new JobDescription();
        assertNotNull(jd.getSkills());
        assertEquals(0, jd.getSkills().size());
    }
}