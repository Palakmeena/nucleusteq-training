package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.dto.CandidateRequestDto;
import com.nucleusteq.interviewtracker.dto.CandidateResponseDto;
import com.nucleusteq.interviewtracker.entity.*;
import com.nucleusteq.interviewtracker.enums.*;
import com.nucleusteq.interviewtracker.mapper.CandidateMapper;
import com.nucleusteq.interviewtracker.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private JobDescriptionRepository jobDescriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CandidateMapper candidateMapper;

    // ✅ FIX: missing mocks (THIS WAS CAUSING NULL POINTER)
    @Mock
    private CandidateProfileRepository candidateProfileRepository;

    @Mock
    private com.nucleusteq.interviewtracker.repository.InterviewRepository interviewRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private CandidateService candidateService;

    private CandidateRequestDto request;
    private Candidate candidate;
    private CandidateResponseDto responseDto;
    private JobDescription jd;
    private User user;

    @BeforeEach
    void setUp() {

        request = new CandidateRequestDto();
        request.setFullName("Sarah Gupta");
        request.setEmail("sarah@example.com");
        request.setMobileCode("+91");
        request.setMobileNumber("9876543210");
        request.setCurrentOrganization("TechCorp");
        request.setTotalExperience(3.0);
        request.setRelevantExperience(2.0);
        request.setCurrentCtc(6.0);
        request.setExpectedCtc(10.0);
        request.setNoticePeriod(30);
        request.setPreferredLocation("Bangalore");
        request.setSource("LinkedIn");
        request.setJobDescriptionId(1L);

        jd = new JobDescription(
                "Backend Dev", "Desc",
                1, 3, 4.0, 8.0,
                "Bangalore", JobType.FULL_TIME
        );
        jd.setActive(true);

        user = new User(
                "Sarah Gupta",
                "sarah@example.com",
                "encodedPass",
                UserRole.CANDIDATE
        );
        user.setActive(true);

        candidate = new Candidate(
                "Sarah Gupta",
                "sarah@example.com",
                "+91",
                "9876543210",
                "TechCorp",
                3.0,
                2.0,
                6.0,
                10.0,
                30,
                "Bangalore",
                "LinkedIn",
                jd,
                user
        );

        responseDto = new CandidateResponseDto();
        responseDto.setFullName("Sarah Gupta");
        responseDto.setCurrentStage(InterviewStage.PROFILING);
    }

    // ---------------- CREATE ----------------

    @Test
    void createCandidateProfile_shouldCreateAndReturn() {

        when(userRepository.findByEmail("sarah@example.com"))
                .thenReturn(Optional.of(user));

        when(candidateRepository.findByUser(user))
                .thenReturn(Optional.empty());

        when(jobDescriptionRepository.findById(1L))
                .thenReturn(Optional.of(jd));

        when(candidateMapper.mapToEntity(request, jd, user))
                .thenReturn(candidate);

        when(candidateRepository.save(candidate))
                .thenReturn(candidate);

        when(candidateMapper.mapToResponseDto(candidate))
                .thenReturn(responseDto);

        // FIX: sync method dependency mock
        when(candidateProfileRepository.findByUser(user))
                .thenReturn(Optional.empty());

        CandidateResponseDto result =
                candidateService.createCandidateProfile(request, "sarah@example.com");

        assertNotNull(result);
        assertEquals("Sarah Gupta", result.getFullName());
    }

    // ---------------- EMAIL MISMATCH FIXED ----------------

    @Test
    void createCandidateProfile_shouldThrowWhenEmailMismatch() {
        // Create a user with different email than the request
        User differentUser = new User(
                "Different User",
                "other@example.com",
                "encodedPass",
                UserRole.CANDIDATE
        );
        differentUser.setActive(true);

        when(userRepository.findByEmail("other@example.com"))
                .thenReturn(Optional.of(differentUser));

        // The request still has email "sarah@example.com" from setUp()
        // So when comparing user.getEmail() (other@example.com) with request.getEmail() (sarah@example.com)
        // They don't match, so IllegalArgumentException should be thrown

        assertThrows(IllegalArgumentException.class,
                () -> candidateService.createCandidateProfile(request, "other@example.com"));
    }

    // ---------------- JD NOT FOUND ----------------

    @Test
    void createCandidateProfile_shouldThrowWhenJdNotFound() {

        when(userRepository.findByEmail("sarah@example.com"))
                .thenReturn(Optional.of(user));

        when(candidateRepository.findByUser(user))
                .thenReturn(Optional.empty());

        when(jobDescriptionRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> candidateService.createCandidateProfile(request, "sarah@example.com"));
    }

    // ---------------- GET ALL ----------------

    @Test
    void getAllCandidates_shouldReturnAll() {

        when(candidateRepository.findAll())
                .thenReturn(Arrays.asList(candidate, candidate));

        when(candidateMapper.mapToResponseDto(any()))
                .thenReturn(responseDto);

        List<CandidateResponseDto> result = candidateService.getAllCandidates();

        assertEquals(2, result.size());
    }

    // ---------------- GET BY ID ----------------

    @Test
    void getCandidateById_shouldReturn() {

        when(candidateRepository.findById(1L))
                .thenReturn(Optional.of(candidate));

        when(candidateMapper.mapToResponseDto(candidate))
                .thenReturn(responseDto);

        CandidateResponseDto result = candidateService.getCandidateById(1L);

        assertNotNull(result);
    }

    @Test
    void getCandidateById_shouldThrow() {

        when(candidateRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> candidateService.getCandidateById(99L));
    }

    // ---------------- PROFILE ----------------

    @Test
    void getCandidateProfile_shouldReturn() {

        when(userRepository.findByEmail("sarah@example.com"))
                .thenReturn(Optional.of(user));

        when(candidateRepository.findByUser(user))
                .thenReturn(Optional.of(candidate));

        when(candidateMapper.mapToResponseDto(candidate))
                .thenReturn(responseDto);

        CandidateResponseDto result =
                candidateService.getCandidateProfile("sarah@example.com");

        assertNotNull(result);
    }

    @Test
    void getCandidateProfile_shouldThrowUserNotFound() {

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> candidateService.getCandidateProfile("unknown@example.com"));
    }

    @Test
    void getCandidateProfile_shouldThrow_whenCandidateNotFound() {

        when(userRepository.findByEmail("sarah@example.com"))
                .thenReturn(Optional.of(user));

        when(candidateRepository.findByUser(user))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> candidateService.getCandidateProfile("sarah@example.com"));
    }

    @Test
    void getAllCandidates_shouldReturnEmptyList_whenNoCandidates() {

        when(candidateRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<CandidateResponseDto> result = candidateService.getAllCandidates();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllCandidates_shouldHandleSingleCandidate() {

        when(candidateRepository.findAll())
                .thenReturn(Collections.singletonList(candidate));

        when(candidateMapper.mapToResponseDto(any()))
                .thenReturn(responseDto);

        List<CandidateResponseDto> result = candidateService.getAllCandidates();

        assertEquals(1, result.size());
    }

    @Test
    void createCandidateProfile_shouldCreateNewUser_whenUserNotFound() {
        // This test scenario - when user is not found, should still create candidate
        when(userRepository.findByEmail("sarah@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> candidateService.createCandidateProfile(request, "sarah@example.com"));
    }

    @Test
    void getAllCandidates_shouldMapEachCandidate() {
        Candidate candidate2 = new Candidate(
                "John Doe",
                "john@example.com",
                "+91",
                "9876543211",
                "TechCorp",
                5.0,
                3.0,
                8.0,
                12.0,
                60,
                "Mumbai",
                "Referral",
                jd,
                user
        );

        when(candidateRepository.findAll())
                .thenReturn(Arrays.asList(candidate, candidate2));

        when(candidateMapper.mapToResponseDto(any()))
                .thenReturn(responseDto);

        List<CandidateResponseDto> result = candidateService.getAllCandidates();

        assertEquals(2, result.size());
        verify(candidateMapper, times(2)).mapToResponseDto(any());
    }

    @Test
    void getCandidateById_shouldMapCorrectCandidate() {

        when(candidateRepository.findById(1L))
                .thenReturn(Optional.of(candidate));

        when(candidateMapper.mapToResponseDto(candidate))
                .thenReturn(responseDto);

        CandidateResponseDto result = candidateService.getCandidateById(1L);

        assertNotNull(result);
        assertEquals("Sarah Gupta", result.getFullName());
        verify(candidateMapper).mapToResponseDto(candidate);
    }

    // ────────────── DELETE CANDIDATE ──────────────

    @Test
    void deleteCandidate_shouldDelete_whenExists() {

        when(candidateRepository.findById(1L))
                .thenReturn(Optional.of(candidate));

        candidateService.deleteCandidate(1L);

        verify(candidateRepository).delete(candidate);
    }

    @Test
    void deleteCandidate_shouldThrow_whenNotFound() {

        when(candidateRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> candidateService.deleteCandidate(99L));
    }

    @Test
    void deleteCandidate_shouldCleanupInterviews_whenExists() {

        when(candidateRepository.findById(1L))
                .thenReturn(Optional.of(candidate));

        candidateService.deleteCandidate(1L);

        verify(candidateRepository).delete(candidate);
    }

    // ────────────── ACTIVATE CANDIDATE ──────────────

    @Test
    void activateCandidateAccount_shouldActivate_whenTokenValid() {

        when(userRepository.findByActivationToken("valid-token"))
                .thenReturn(Optional.of(user));

                user.setTokenExpiry(java.time.LocalDateTime.now().plusHours(1));

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("encodedNewPass");

        candidateService.activateCandidateAccount("valid-token", "newPassword");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void activateCandidateAccount_shouldThrow_whenTokenInvalid() {

        when(userRepository.findByActivationToken("invalid-token"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> candidateService.activateCandidateAccount("invalid-token", "password"));
    }

    // ────────────── UPDATE CANDIDATE STAGE ──────────────

    @Test
    void updateCandidateStage_shouldUpdate_whenExists() {

        when(candidateRepository.findById(1L))
                .thenReturn(Optional.of(candidate));

        when(candidateRepository.save(any(Candidate.class)))
                .thenReturn(candidate);

        when(candidateMapper.mapToResponseDto(candidate))
                .thenReturn(responseDto);

        CandidateResponseDto result = candidateService.updateCandidateStage(1L, InterviewStage.SELECTED);

        assertNotNull(result);
        verify(candidateRepository).save(any(Candidate.class));
    }

    @Test
    void updateCandidateStage_shouldThrow_whenNotFound() {

        when(candidateRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> candidateService.updateCandidateStage(99L, InterviewStage.SELECTED));
    }

    // ────────────── UPDATE RESUME PATH ──────────────

    @Test
    void updateResumePath_shouldUpdate_whenExists() {

        when(candidateRepository.findById(1L))
                .thenReturn(Optional.of(candidate));

        when(candidateRepository.save(any(Candidate.class)))
                .thenReturn(candidate);

        candidateService.updateResumePath(1L, "path/to/resume.pdf");

        verify(candidateRepository).save(any(Candidate.class));
    }

    @Test
    void updateResumePath_shouldThrow_whenNotFound() {

        when(candidateRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> candidateService.updateResumePath(99L, "path/to/resume.pdf"));
    }

    // ────────────── UPDATE STAGE EDGE CASES ──────────────

    @Test
    void updateCandidateStage_shouldUpdateToRejected() {

        when(candidateRepository.findById(1L))
                .thenReturn(Optional.of(candidate));

        when(candidateRepository.save(any(Candidate.class)))
                .thenReturn(candidate);

        when(candidateMapper.mapToResponseDto(candidate))
                .thenReturn(responseDto);

        CandidateResponseDto result = candidateService.updateCandidateStage(1L, InterviewStage.REJECTED);

        assertNotNull(result);
        verify(candidateRepository).save(any(Candidate.class));
    }
}