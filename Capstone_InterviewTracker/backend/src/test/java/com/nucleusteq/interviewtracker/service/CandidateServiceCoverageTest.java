package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.dto.CandidateRequestDto;
import com.nucleusteq.interviewtracker.dto.CandidateResponseDto;
import com.nucleusteq.interviewtracker.entity.Candidate;
import com.nucleusteq.interviewtracker.entity.JobDescription;
import com.nucleusteq.interviewtracker.entity.Interview;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.enums.InterviewStage;
import com.nucleusteq.interviewtracker.enums.UserRole;
import com.nucleusteq.interviewtracker.mapper.CandidateMapper;
import com.nucleusteq.interviewtracker.repository.CandidateRepository;
import com.nucleusteq.interviewtracker.repository.InterviewRepository;
import com.nucleusteq.interviewtracker.repository.JobDescriptionRepository;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidateServiceCoverageTest {

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

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private CandidateService candidateService;

    @Test
    void createCandidateProfileByHr_shouldCreateUserCandidateAndSendActivationEmail() {
        CandidateRequestDto request = new CandidateRequestDto();
        request.setFullName("Maya Singh");
        request.setEmail("maya@example.com");
        request.setMobileNumber("9999999999");
        request.setJobDescriptionId(12L);

        JobDescription jobDescription = new JobDescription();
        jobDescription.setId(12L);
        jobDescription.setJobTitle("Backend Engineer");
        jobDescription.setActive(true);

        Candidate candidate = new Candidate();
        candidate.setId(7L);

        CandidateResponseDto responseDto = new CandidateResponseDto();
        responseDto.setFullName("Maya Singh");

        when(userRepository.existsByEmail("maya@example.com")).thenReturn(false);
        when(candidateRepository.existsByMobileNumber("9999999999")).thenReturn(false);
        when(jobDescriptionRepository.findById(12L)).thenReturn(Optional.of(jobDescription));
        when(passwordEncoder.encode(any())).thenReturn("encoded-temp-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(candidateMapper.mapToEntity(any(), any(), any())).thenReturn(candidate);
        when(candidateRepository.save(candidate)).thenReturn(candidate);
        when(candidateMapper.mapToResponseDto(candidate)).thenReturn(responseDto);

        CandidateResponseDto result = candidateService.createCandidateProfileByHr(request);

        assertNotNull(result);
        assertEquals("Maya Singh", result.getFullName());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("maya@example.com", savedUser.getEmail());
        assertEquals(UserRole.CANDIDATE, savedUser.getRole());
        assertNotNull(savedUser.getActivationToken());
        assertNotNull(savedUser.getTokenExpiry());
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void createCandidateProfile_shouldThrowWhenActiveCandidateAlreadyExists() {
        CandidateRequestDto request = new CandidateRequestDto();
        request.setEmail("maya@example.com");
        request.setJobDescriptionId(12L);

        User loggedInUser = new User("Maya Singh", "maya@example.com", "pwd", UserRole.CANDIDATE);
        Candidate activeCandidate = new Candidate();
        activeCandidate.setCurrentStage(InterviewStage.SCREENING);

        when(userRepository.findByEmail("maya@example.com")).thenReturn(Optional.of(loggedInUser));
        when(candidateRepository.findByUser(loggedInUser)).thenReturn(Optional.of(activeCandidate));

        assertThrows(IllegalArgumentException.class,
                () -> candidateService.createCandidateProfile(request, "maya@example.com"));
    }

    @Test
    void createCandidateProfile_shouldReuseProfileWhenRejected() {
        CandidateRequestDto request = new CandidateRequestDto();
        request.setEmail("maya@example.com");
        request.setJobDescriptionId(12L);

        User loggedInUser = new User("Maya Singh", "maya@example.com", "pwd", UserRole.CANDIDATE);
        Candidate rejectedCandidate = new Candidate();
        rejectedCandidate.setCurrentStage(InterviewStage.REJECTED);

        JobDescription jobDescription = new JobDescription();
        jobDescription.setId(12L);
        jobDescription.setJobTitle("Backend Engineer");
        jobDescription.setActive(true);

        CandidateResponseDto responseDto = new CandidateResponseDto();
        responseDto.setEmail("maya@example.com");

        when(userRepository.findByEmail("maya@example.com")).thenReturn(Optional.of(loggedInUser));
        when(candidateRepository.findByUser(loggedInUser)).thenReturn(Optional.of(rejectedCandidate));
        when(jobDescriptionRepository.findById(12L)).thenReturn(Optional.of(jobDescription));
        when(candidateRepository.save(rejectedCandidate)).thenReturn(rejectedCandidate);
        when(candidateMapper.mapToResponseDto(rejectedCandidate)).thenReturn(responseDto);

        CandidateResponseDto result = candidateService.createCandidateProfile(request, "maya@example.com");

        assertNotNull(result);
        assertEquals(InterviewStage.PROFILING, rejectedCandidate.getCurrentStage());
        assertEquals(jobDescription, rejectedCandidate.getJobDescription());
        verify(candidateRepository).save(rejectedCandidate);
    }

    @Test
    void createCandidateProfileByHr_shouldRejectDuplicateEmail() {
        CandidateRequestDto request = new CandidateRequestDto();
        request.setEmail("dup@example.com");
        request.setMobileNumber("9999999999");

        when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> candidateService.createCandidateProfileByHr(request));
    }

    @Test
    void createCandidateProfileByHr_shouldRejectInactiveJobDescription() {
        CandidateRequestDto request = new CandidateRequestDto();
        request.setEmail("new@example.com");
        request.setMobileNumber("9999999999");
        request.setFullName("New User");
        request.setJobDescriptionId(99L);

        JobDescription jobDescription = new JobDescription();
        jobDescription.setId(99L);
        jobDescription.setActive(false);

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(candidateRepository.existsByMobileNumber("9999999999")).thenReturn(false);
        when(jobDescriptionRepository.findById(99L)).thenReturn(Optional.of(jobDescription));

        assertThrows(IllegalArgumentException.class,
                () -> candidateService.createCandidateProfileByHr(request));
    }

    @Test
    void getCandidateProfile_shouldReturnResponse() {
        User loggedInUser = new User("Maya Singh", "maya@example.com", "pwd", UserRole.CANDIDATE);
        Candidate candidate = new Candidate();
        CandidateResponseDto responseDto = new CandidateResponseDto();
        responseDto.setEmail("maya@example.com");

        when(userRepository.findByEmail("maya@example.com")).thenReturn(Optional.of(loggedInUser));
        when(candidateRepository.findByUser(loggedInUser)).thenReturn(Optional.of(candidate));
        when(candidateMapper.mapToResponseDto(candidate)).thenReturn(responseDto);

        CandidateResponseDto result = candidateService.getCandidateProfile("maya@example.com");

        assertNotNull(result);
        assertEquals("maya@example.com", result.getEmail());
    }

    @Test
    void updateResumePath_shouldPersistResumePath() {
        Candidate candidate = new Candidate();
        candidate.setId(33L);
        when(candidateRepository.findById(33L)).thenReturn(Optional.of(candidate));

        candidateService.updateResumePath(33L, "https://drive.google.com/file/d/123/preview");

        assertEquals("https://drive.google.com/file/d/123/preview", candidate.getResumePath());
        verify(candidateRepository).save(candidate);
    }

    @Test
    void updateCandidateStage_shouldPersistStage() {
        Candidate candidate = new Candidate();
        candidate.setId(44L);
        CandidateResponseDto responseDto = new CandidateResponseDto();
        responseDto.setEmail("stage@example.com");

        when(candidateRepository.findById(44L)).thenReturn(Optional.of(candidate));
        when(candidateRepository.save(candidate)).thenReturn(candidate);
        when(candidateMapper.mapToResponseDto(candidate)).thenReturn(responseDto);

        CandidateResponseDto result = candidateService.updateCandidateStage(44L, InterviewStage.SCREENING);

        assertNotNull(result);
        assertEquals(InterviewStage.SCREENING, candidate.getCurrentStage());
    }

    @Test
    void deleteCandidate_shouldSkipUserDeleteWhenNoUserLinked() {
        Candidate candidate = new Candidate();
        candidate.setId(66L);

        when(candidateRepository.findById(66L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.findByCandidate(candidate)).thenReturn(List.of());

        candidateService.deleteCandidate(66L);

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void activateCandidateAccount_shouldRejectInvalidToken() {
        when(userRepository.findByActivationToken("missing-token")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> candidateService.activateCandidateAccount("missing-token", "new-password"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteCandidate_shouldDeleteInterviewsCandidateAndLinkedUser() {
        User user = new User("Asha", "asha@example.com", "pwd", UserRole.CANDIDATE);
        user.setId(20L);

        Candidate candidate = new Candidate();
        candidate.setId(8L);
        candidate.setUser(user);

        Interview interviewOne = new Interview();
        interviewOne.setId(100L);
        Interview interviewTwo = new Interview();
        interviewTwo.setId(101L);

        when(candidateRepository.findById(8L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.findByCandidate(candidate)).thenReturn(List.of(interviewOne, interviewTwo));

        candidateService.deleteCandidate(8L);

        InOrder order = inOrder(interviewRepository, candidateRepository, userRepository);
        order.verify(interviewRepository).deleteAll(List.of(interviewOne, interviewTwo));
        order.verify(candidateRepository).delete(candidate);
        order.verify(userRepository).delete(user);
    }

    @Test
    void activateCandidateAccount_shouldSetPasswordAndClearToken() {
        User user = new User("Ravi", "ravi@example.com", "old", UserRole.CANDIDATE);
        user.setActivationToken("token-123");
        user.setTokenExpiry(LocalDateTime.now().plusHours(1));

        when(userRepository.findByActivationToken("token-123")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");

        candidateService.activateCandidateAccount("token-123", "new-password");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("encoded-new-password", savedUser.getPassword());
        assertEquals(true, savedUser.isActive());
        assertEquals(null, savedUser.getActivationToken());
        assertEquals(null, savedUser.getTokenExpiry());
    }

    @Test
    void activateCandidateAccount_shouldRejectExpiredToken() {
        User user = new User("Ravi", "ravi@example.com", "old", UserRole.CANDIDATE);
        user.setActivationToken("expired-token");
        user.setTokenExpiry(LocalDateTime.now().minusHours(1));

        when(userRepository.findByActivationToken("expired-token")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> candidateService.activateCandidateAccount("expired-token", "new-password"));

        verify(userRepository, never()).save(any(User.class));
    }
}
