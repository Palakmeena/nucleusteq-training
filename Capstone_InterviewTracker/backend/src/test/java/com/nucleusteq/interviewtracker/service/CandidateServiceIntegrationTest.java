package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.entity.JobDescription;
import com.nucleusteq.interviewtracker.entity.Candidate;
import com.nucleusteq.interviewtracker.enums.UserRole;
import com.nucleusteq.interviewtracker.enums.JobType;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import com.nucleusteq.interviewtracker.repository.CandidateRepository;
import com.nucleusteq.interviewtracker.repository.InterviewRepository;
import com.nucleusteq.interviewtracker.repository.JobDescriptionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CandidateServiceIntegrationTest {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private JobDescriptionRepository jobDescriptionRepository;
    @Test
    void activateCandidateAccount_shouldActivate_whenTokenValid() {
        User user = new User("Test User", "test.activate@example.com", "temp", UserRole.CANDIDATE);
        user.setActive(false);
        user.setActivationToken("valid-token-123");
        user.setTokenExpiry(LocalDateTime.now().plusHours(2));
        User saved = userRepository.save(user);

        candidateService.activateCandidateAccount("valid-token-123", "new-secret");

        User updated = userRepository.findById(saved.getId()).orElseThrow();
        assertTrue(updated.isActive());
        assertNull(updated.getActivationToken());
        assertNull(updated.getTokenExpiry());
        assertTrue(passwordEncoder.matches("new-secret", updated.getPassword()));
    }

    @Test
    void activateCandidateAccount_shouldThrow_whenTokenExpired() {
        User user = new User("Expired User", "expired.activate@example.com", "temp", UserRole.CANDIDATE);
        user.setActive(false);
        user.setActivationToken("expired-token-123");
        user.setTokenExpiry(LocalDateTime.now().minusHours(1));
        userRepository.save(user);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> candidateService.activateCandidateAccount("expired-token-123", "pw"));
        assertTrue(ex.getMessage().toLowerCase().contains("expired"));
    }

    @Test
    void deleteCandidate_shouldRemoveAllRelatedEntities() {
        // create job description
        JobDescription jd = new JobDescription("Dev", "desc", 1, 3, 1.0, 2.0, "Remote", JobType.REMOTE);
        jd = jobDescriptionRepository.save(jd);

        // create user and candidate
        User user = new User("To Delete", "todelete@example.com", "pw", UserRole.CANDIDATE);
        user = userRepository.save(user);

        Candidate candidate = new Candidate("+91", "9999999999",
            "Org", 1.0, 1.0, 1.0, 1.0, 30, "City", "Referral", jd, user);
        candidate = candidateRepository.save(candidate);

        // perform delete
        candidateService.deleteCandidate(candidate.getId());

        assertFalse(candidateRepository.findById(candidate.getId()).isPresent());
        assertFalse(userRepository.findById(user.getId()).isPresent());
        assertTrue(interviewRepository.findByCandidate(candidate).isEmpty());
    }

}
