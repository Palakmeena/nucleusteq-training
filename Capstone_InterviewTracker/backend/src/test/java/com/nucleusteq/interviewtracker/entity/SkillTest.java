package com.nucleusteq.interviewtracker.entity;

import com.nucleusteq.interviewtracker.enums.JobType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Skill entity.
 * Tests that skill is created correctly
 * and linked to a JobDescription properly.
 */
class SkillTest {

    /**
     * Helper method to create a sample JobDescription
     * so we don't repeat this code in every test.
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

    @Test
    void shouldCreateSkillWithConstructor() {
        /** Arrange. */
        String skillName = "Java";
        JobDescription jd = createSampleJobDescription();

        /** Act. */
        Skill skill = new Skill(skillName, jd);

        /** Assert. */
        assertEquals(skillName, skill.getSkillName());
        assertEquals(jd, skill.getJobDescription());
    }

    @Test
    void shouldSetAndGetSkillName() {
        /** Test setter and getter work correctly. */
        Skill skill = new Skill();
        skill.setSkillName("Spring Boot");
        assertEquals("Spring Boot", skill.getSkillName());
    }

    @Test
    void shouldSetAndGetJobDescription() {
        /** Test that skill is correctly linked to a JD. */
        JobDescription jd = createSampleJobDescription();
        Skill skill = new Skill();
        skill.setJobDescription(jd);
        assertEquals(jd, skill.getJobDescription());
    }

    @Test
    void shouldSetAndGetId() {
        Skill skill = new Skill();
        skill.setId(1L);
        assertEquals(1L, skill.getId());
    }

    @Test
    void shouldAllowDifferentSkillsForSameJd() {
        /** Multiple skills can belong to the same JD. */
        JobDescription jd = createSampleJobDescription();

        Skill skill1 = new Skill("Java", jd);
        Skill skill2 = new Skill("Spring Boot", jd);
        Skill skill3 = new Skill("MySQL", jd);

        /** All skills should link to the same JD. */
        assertEquals(jd, skill1.getJobDescription());
        assertEquals(jd, skill2.getJobDescription());
        assertEquals(jd, skill3.getJobDescription());

        /** But skill names should be different. */
        assertNotEquals(skill1.getSkillName(), skill2.getSkillName());
        assertNotEquals(skill2.getSkillName(), skill3.getSkillName());
    }
}