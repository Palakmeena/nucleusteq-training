package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.dto.CandidateRequestDto;
import com.nucleusteq.interviewtracker.entity.JobDescription;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.entity.Candidate;
import com.nucleusteq.interviewtracker.enums.JobType;
import com.nucleusteq.interviewtracker.enums.UserRole;
import com.nucleusteq.interviewtracker.enums.InterviewStage;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import com.nucleusteq.interviewtracker.repository.CandidateRepository;
import com.nucleusteq.interviewtracker.repository.JobDescriptionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CandidateServiceAdditionalIntegrationTest {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private JobDescriptionRepository jobDescriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Test
    void createCandidateProfileByHr_shouldCreateUserAndCandidate() {
        JobDescription jd = new JobDescription("Backend Dev", "desc", 1, 3, 1.0, 2.0, "Onsite", JobType.REMOTE);
        jd.setActive(true);
        jd = jobDescriptionRepository.save(jd);

        CandidateRequestDto req = new CandidateRequestDto();
        req.setFullName("HR Created");
        req.setEmail("hr.created@example.com");
        req.setMobileCode("+91");
        req.setMobileNumber("9998887777");
        req.setCurrentOrganization("Org");
        req.setTotalExperience(2.0);
        req.setRelevantExperience(1.0);
        req.setCurrentCtc(3.0);
        req.setExpectedCtc(4.0);
        req.setNoticePeriod(30);
        req.setPreferredLocation("City");
        req.setSource("Referral");
        req.setGender("Other");
        req.setJobDescriptionId(jd.getId());

        var resp = candidateService.createCandidateProfileByHr(req);

        assertNotNull(resp);
        assertEquals("HR Created", resp.getFullName());
        assertEquals("hr.created@example.com", resp.getEmail());

        // user should be created and inactive with token
        User user = userRepository.findByEmail("hr.created@example.com").orElseThrow();
        assertFalse(user.isActive());
        assertNotNull(user.getActivationToken());
        assertNotNull(user.getTokenExpiry());

        // candidate entity should be linked to the created user
        assertTrue(candidateRepository.findByUser(user).isPresent());
    }

    @Test
    void updateResumePath_shouldUpdateCandidateResume() {
        // prepare candidate
        User user = new User("Resume User", "resume.user@example.com", "pw", UserRole.CANDIDATE);
        user = userRepository.save(user);
        JobDescription jd = new JobDescription("X", "x", 1, 1, 1.0, 1.0, "L", JobType.REMOTE);
        jd.setActive(true);
        jd = jobDescriptionRepository.save(jd);

        Candidate candidate = new Candidate("+91", "9111111111",
                "Org", 1.0, 1.0, 1.0, 1.0, 25, "City", "src", jd, user);
        candidate = candidateRepository.save(candidate);

        candidateService.updateResumePath(candidate.getId(), "/tmp/resume.pdf");

        Candidate updated = candidateRepository.findById(candidate.getId()).orElseThrow();
        assertEquals("/tmp/resume.pdf", updated.getResumePath());
    }

    @Test
    void updateCandidateStage_shouldChangeStage() {
        User user = new User("Stage User", "stage.user@example.com", "pw", UserRole.CANDIDATE);
        user = userRepository.save(user);
        JobDescription jd = new JobDescription("J", "j", 1, 1, 1.0, 1.0, "L", JobType.REMOTE);
        jd.setActive(true);
        jd = jobDescriptionRepository.save(jd);

        Candidate candidate = new Candidate("+91", "9222222222",
                "Org", 1.0, 1.0, 1.0, 1.0, 26, "City", "src", jd, user);
        candidate = candidateRepository.save(candidate);

        var dto = candidateService.updateCandidateStage(candidate.getId(), InterviewStage.L1_TECHNICAL);
        assertEquals(InterviewStage.L1_TECHNICAL, dto.getCurrentStage());
    }

    @Test
    void createCandidateProfileByHr_shouldRejectDuplicateEmail() {
        // create existing user
        User existing = new User("Existing", "dup@example.com", "pw", UserRole.CANDIDATE);
        existing = userRepository.save(existing);

        JobDescription jd = new JobDescription("J2", "j2", 1, 1, 1.0, 1.0, "L", JobType.REMOTE);
        jd.setActive(true);
        jd = jobDescriptionRepository.save(jd);

        CandidateRequestDto req = new CandidateRequestDto();
        req.setFullName("Dup");
        req.setEmail("dup@example.com");
        req.setMobileCode("+91");
        req.setMobileNumber("9000000000");
        req.setCurrentOrganization("Org");
        req.setTotalExperience(1.0);
        req.setRelevantExperience(1.0);
        req.setCurrentCtc(1.0);
        req.setExpectedCtc(1.0);
        req.setNoticePeriod(0);
        req.setPreferredLocation("City");
        req.setSource("src");
        req.setGender("M");
        req.setJobDescriptionId(jd.getId());

        assertThrows(IllegalArgumentException.class, () -> candidateService.createCandidateProfileByHr(req));
    }
}
