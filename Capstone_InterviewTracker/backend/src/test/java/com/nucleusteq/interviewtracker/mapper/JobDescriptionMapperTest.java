package com.nucleusteq.interviewtracker.mapper;

import com.nucleusteq.interviewtracker.dto.JobDescriptionRequestDto;
import com.nucleusteq.interviewtracker.dto.JobDescriptionResponseDto;
import com.nucleusteq.interviewtracker.entity.JobDescription;
import com.nucleusteq.interviewtracker.entity.Skill;
import com.nucleusteq.interviewtracker.enums.JobType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for JobDescriptionMapper.
 * Verifies mapping between JD entity and DTOs.
 */
class JobDescriptionMapperTest {

    private JobDescriptionMapper mapper;

    /** Sample request DTO used across tests. */
    private static final List<String> SKILLS = Arrays.asList("Java", "Spring Boot", "PostgreSQL");

    @BeforeEach
    void setUp() {
        mapper = new JobDescriptionMapper();
    }

    private JobDescriptionRequestDto buildRequest() {
        JobDescriptionRequestDto req = new JobDescriptionRequestDto();
        req.setJobTitle("Backend Developer");
        req.setJobDescription("Develop REST APIs using Spring Boot");
        req.setMinExperience(1);
        req.setMaxExperience(3);
        req.setMinSalary(4.0);
        req.setMaxSalary(8.0);
        req.setLocation("Bangalore");
        req.setJobType(JobType.FULL_TIME);
        req.setSkills(SKILLS);
        return req;
    }

    @Test
    void mapToEntity_shouldMapAllFieldsCorrectly() {
        JobDescriptionRequestDto request = buildRequest();

        JobDescription result = mapper.mapToEntity(request);

        assertNotNull(result);
        assertEquals("Backend Developer", result.getJobTitle());
        assertEquals("Bangalore", result.getLocation());
        assertEquals(1, result.getMinExperience());
        assertEquals(3, result.getMaxExperience());
        assertEquals(4.0, result.getMinSalary());
        assertEquals(8.0, result.getMaxSalary());
        assertEquals(JobType.FULL_TIME, result.getJobType());
    }

    @Test
    void mapToEntity_shouldCreateSkillEntities() {
        JobDescriptionRequestDto request = buildRequest();

        JobDescription result = mapper.mapToEntity(request);

        assertNotNull(result.getSkills());
        assertEquals(3, result.getSkills().size());
        assertTrue(result.getSkills().stream().anyMatch(s -> s.getSkillName().equals("Java")));
        assertTrue(result.getSkills().stream().anyMatch(s -> s.getSkillName().equals("Spring Boot")));
    }

    @Test
    void mapToEntity_shouldTrimSkillNames() {
        JobDescriptionRequestDto request = buildRequest();
        request.setSkills(Arrays.asList("  Java  ", " MySQL "));

        JobDescription result = mapper.mapToEntity(request);

        assertTrue(result.getSkills().stream().anyMatch(s -> s.getSkillName().equals("Java")));
        assertTrue(result.getSkills().stream().anyMatch(s -> s.getSkillName().equals("MySQL")));
    }

    @Test
    void mapToResponseDto_shouldMapAllFieldsCorrectly() {
        JobDescription jd = new JobDescription(
                "Backend Developer", "Description", 1, 3, 4.0, 8.0, "Bangalore", JobType.FULL_TIME
        );
        jd.getSkills().add(new Skill("Java", jd));
        jd.getSkills().add(new Skill("Spring Boot", jd));

        JobDescriptionResponseDto result = mapper.mapToResponseDto(jd);

        assertNotNull(result);
        assertEquals("Backend Developer", result.getJobTitle());
        assertEquals("Bangalore", result.getLocation());
        assertEquals(2, result.getSkills().size());
        assertTrue(result.isActive());
    }

    @Test
    void updateEntityFromRequest_shouldUpdateFieldsCorrectly() {
        JobDescription jd = new JobDescription(
                "Old Title", "Old Desc", 1, 3, 4.0, 8.0, "Pune", JobType.CONTRACT
        );
        jd.getSkills().add(new Skill("OldSkill", jd));

        JobDescriptionRequestDto request = buildRequest();
        request.setJobTitle("Updated Title");
        request.setLocation("Hyderabad");

        mapper.updateEntityFromRequest(jd, request);

        assertEquals("Updated Title", jd.getJobTitle());
        assertEquals("Hyderabad", jd.getLocation());
        assertEquals(3, jd.getSkills().size());
    }

    @Test
    void updateEntityFromRequest_shouldReplaceOldSkills() {
        JobDescription jd = new JobDescription(
                "Title", "Desc", 1, 3, 4.0, 8.0, "Delhi", JobType.REMOTE
        );
        jd.getSkills().add(new Skill("OldSkill1", jd));
        jd.getSkills().add(new Skill("OldSkill2", jd));

        JobDescriptionRequestDto request = buildRequest();
        request.setSkills(Arrays.asList("React", "TypeScript"));

        mapper.updateEntityFromRequest(jd, request);

        assertEquals(2, jd.getSkills().size());
        assertTrue(jd.getSkills().stream().anyMatch(s -> s.getSkillName().equals("React")));
    }
}
