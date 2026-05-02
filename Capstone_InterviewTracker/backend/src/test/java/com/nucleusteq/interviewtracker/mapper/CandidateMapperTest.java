package com.nucleusteq.interviewtracker.mapper;

import com.nucleusteq.interviewtracker.dto.CandidateRequestDto;
import com.nucleusteq.interviewtracker.dto.CandidateResponseDto;
import com.nucleusteq.interviewtracker.entity.Candidate;
import com.nucleusteq.interviewtracker.entity.JobDescription;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.enums.InterviewStage;
import com.nucleusteq.interviewtracker.enums.JobType;
import com.nucleusteq.interviewtracker.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for CandidateMapper.
 * Verifies entity building and DTO mapping logic.
 */
class CandidateMapperTest {

    private CandidateMapper mapper;
    private JobDescription jd;
    private User user;

    @BeforeEach
    void setUp() {
        mapper = new CandidateMapper();
        jd = new JobDescription("Backend Dev", "Description", 1, 3, 4.0, 8.0, "Bangalore", JobType.FULL_TIME);
        user = new User("Sarah Gupta", "sarah@example.com", "encodedPass", UserRole.CANDIDATE);
    }

    private CandidateRequestDto buildRequest() {
        CandidateRequestDto req = new CandidateRequestDto();
        req.setFullName("Sarah Gupta");
        req.setEmail("sarah@example.com");
        req.setMobileCode("+91");
        req.setMobileNumber("9876543210");
        req.setCurrentOrganization("TechCorp");
        req.setTotalExperience(3.0);
        req.setRelevantExperience(2.0);
        req.setCurrentCtc(6.0);
        req.setExpectedCtc(10.0);
        req.setNoticePeriod(30);
        req.setPreferredLocation("Bangalore");
        req.setSource("LinkedIn");
        req.setJobDescriptionId(1L);
        return req;
    }

    @Test
    void mapToEntity_shouldMapAllRequiredFields() {
        CandidateRequestDto request = buildRequest();

        Candidate result = mapper.mapToEntity(request, jd, user);

        assertNotNull(result);
        assertEquals("Sarah Gupta", result.getFullName());
        assertEquals("sarah@example.com", result.getEmail());
        assertEquals("+91", result.getMobileCode());
        assertEquals("9876543210", result.getMobileNumber());
        assertEquals("TechCorp", result.getCurrentOrganization());
        assertEquals(3.0, result.getTotalExperience());
        assertEquals(2.0, result.getRelevantExperience());
        assertEquals(6.0, result.getCurrentCtc());
        assertEquals(10.0, result.getExpectedCtc());
        assertEquals(30, result.getNoticePeriod());
        assertEquals("Bangalore", result.getPreferredLocation());
        assertEquals("LinkedIn", result.getSource());
    }

    @Test
    void mapToEntity_shouldSetStageToProfiling() {
        Candidate result = mapper.mapToEntity(buildRequest(), jd, user);

        assertEquals(InterviewStage.PROFILING, result.getCurrentStage());
    }

    @Test
    void mapToEntity_shouldLinkJdAndUser() {
        Candidate result = mapper.mapToEntity(buildRequest(), jd, user);

        assertEquals(jd, result.getJobDescription());
        assertEquals(user, result.getUser());
    }

    @Test
    void mapToEntity_shouldSetDateOfBirthWhenProvided() {
        CandidateRequestDto request = buildRequest();
        request.setDateOfBirth(LocalDate.of(1999, 5, 15));

        Candidate result = mapper.mapToEntity(request, jd, user);

        assertEquals(LocalDate.of(1999, 5, 15), result.getDateOfBirth());
    }

    @Test
    void mapToEntity_shouldNotSetDateOfBirthWhenNull() {
        CandidateRequestDto request = buildRequest();
        request.setDateOfBirth(null);

        Candidate result = mapper.mapToEntity(request, jd, user);

        assertNull(result.getDateOfBirth());
    }

    @Test
    void mapToResponseDto_shouldMapAllFields() {
        Candidate candidate = mapper.mapToEntity(buildRequest(), jd, user);

        CandidateResponseDto result = mapper.mapToResponseDto(candidate);

        assertNotNull(result);
        assertEquals("Sarah Gupta", result.getFullName());
        assertEquals("sarah@example.com", result.getEmail());
        assertEquals("TechCorp", result.getCurrentOrganization());
        assertEquals(3.0, result.getTotalExperience());
        assertEquals(InterviewStage.PROFILING, result.getCurrentStage());
        assertEquals("Backend Dev", result.getJobTitle());
    }
}
