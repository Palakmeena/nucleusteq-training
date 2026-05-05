package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.dto.FeedbackRequestDto;
import com.nucleusteq.interviewtracker.dto.InterviewRequestDto;
import com.nucleusteq.interviewtracker.dto.InterviewResponseDto;
import com.nucleusteq.interviewtracker.entity.*;
import com.nucleusteq.interviewtracker.enums.FeedbackStatus;
import com.nucleusteq.interviewtracker.enums.InterviewStage;
import com.nucleusteq.interviewtracker.mapper.InterviewMapper;
import com.nucleusteq.interviewtracker.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewServiceCoverageTest {

    @Mock
    InterviewRepository interviewRepository;
    @Mock
    CandidateRepository candidateRepository;
    @Mock
    PanelMemberRepository panelMemberRepository;
    @Mock
    InterviewPanelRepository interviewPanelRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    InterviewMapper interviewMapper;
    @Mock
    FeedbackRepository feedbackRepository;
    @Mock
    JavaMailSender mailSender;

    @InjectMocks
    InterviewService interviewService;

    @BeforeEach
    void setup() {
        // ensure fromEmail is empty so mails still send but without from header
    }

    @Test
    void scheduleInterview_sendsCandidateAndPanelEmails_whenPanelProvided() {
        // Prepare request
        InterviewRequestDto req = new InterviewRequestDto();
        req.setCandidateId(11L);
        req.setInterviewStage(InterviewStage.L1_TECHNICAL);
        req.setInterviewDate(LocalDate.now().plusDays(1));
        req.setInterviewTime(LocalTime.of(10, 0));
        req.setPanelMemberIds(List.of(101L));
        req.setFocusAreas("Java, Spring");

        // Candidate and user
        User user = new User();
        user.setFullName("Test Candidate");
        user.setEmail("candidate@example.com");

        Candidate candidate = new Candidate();
        candidate.setId(11L);
        candidate.setUser(user);
        candidate.setCurrentStage(InterviewStage.SCREENING);

        JobDescription jd = new JobDescription();
        jd.setJobTitle("Backend Dev");
        candidate.setJobDescription(jd);

        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.findByCandidateAndInterviewStageAndIsCompletedFalse(any(), any())).thenReturn(Optional.empty());
        when(interviewRepository.existsByInterviewDateAndInterviewTime(any(), any())).thenReturn(false);

        Interview saved = new Interview(req.getInterviewStage(), req.getInterviewDate(), req.getInterviewTime(), req.getFocusAreas(), candidate);
        saved.setId(55L);
        when(interviewRepository.save(any())).thenReturn(saved);
        when(interviewRepository.findById(55L)).thenReturn(Optional.of(saved));

        PanelMember panel = new PanelMember();
        panel.setId(101L);
        User panelUser = new User();
        panelUser.setFullName("Panel One");
        panelUser.setEmail("panel@example.com");
        panelUser.setActive(true);
        panel.setUser(panelUser);
        when(panelMemberRepository.findById(101L)).thenReturn(Optional.of(panel));

        InterviewResponseDto dto = new InterviewResponseDto();
        when(interviewMapper.mapToResponseDto(any())).thenReturn(dto);

        // Execute
        interviewService.scheduleInterview(req);

        // Verify emails were attempted (candidate + panel)
        verify(mailSender, atLeastOnce()).send(any(SimpleMailMessage.class));
        verify(interviewRepository, times(1)).save(any(Interview.class));
    }

    @Test
    void scheduleInterview_shouldWork_forL2WhenL1Completed() {
        InterviewRequestDto req = new InterviewRequestDto();
        req.setCandidateId(11L);
        req.setInterviewStage(InterviewStage.L2_TECHNICAL);
        req.setInterviewDate(LocalDate.now().plusDays(1));
        req.setInterviewTime(LocalTime.of(11, 0));
        req.setPanelMemberIds(List.of(101L));
        req.setFocusAreas("Core Java");

        Candidate candidate = new Candidate();
        candidate.setId(11L);
        candidate.setUser(new User("Test Candidate", "candidate@example.com", "pwd", com.nucleusteq.interviewtracker.enums.UserRole.CANDIDATE));
        candidate.setCurrentStage(InterviewStage.SCREENING);
        JobDescription jd = new JobDescription();
        jd.setJobTitle("Backend Dev");
        candidate.setJobDescription(jd);

        Interview l1 = new Interview(InterviewStage.L1_TECHNICAL, LocalDate.now().minusDays(2), LocalTime.of(9, 0), "", candidate);
        l1.setCompleted(true);

        PanelMember panel = new PanelMember();
        panel.setUser(new User("Panel One", "panel@example.com", "pwd", com.nucleusteq.interviewtracker.enums.UserRole.PANEL));
        panel.getUser().setActive(true);

        Interview saved = new Interview(req.getInterviewStage(), req.getInterviewDate(), req.getInterviewTime(), req.getFocusAreas(), candidate);
        saved.setId(77L);

        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.findByCandidate(candidate)).thenReturn(List.of(l1));
        when(interviewRepository.findByCandidateAndInterviewStageAndIsCompletedFalse(candidate, InterviewStage.L2_TECHNICAL)).thenReturn(Optional.empty());
        when(interviewRepository.existsByInterviewDateAndInterviewTime(req.getInterviewDate(), req.getInterviewTime())).thenReturn(false);
        when(interviewRepository.save(any(Interview.class))).thenReturn(saved);
        when(interviewRepository.findById(77L)).thenReturn(Optional.of(saved));
        when(panelMemberRepository.findById(101L)).thenReturn(Optional.of(panel));
        when(interviewMapper.mapToResponseDto(any())).thenReturn(new InterviewResponseDto());

        InterviewResponseDto response = interviewService.scheduleInterview(req);

        assertNotNull(response);
    }

    @Test
    void scheduleInterview_shouldWork_forHrRoundWithoutPanels() {
        InterviewRequestDto req = new InterviewRequestDto();
        req.setCandidateId(11L);
        req.setInterviewStage(InterviewStage.HR_ROUND);
        req.setInterviewDate(LocalDate.now().plusDays(1));
        req.setInterviewTime(LocalTime.of(12, 0));
        req.setFocusAreas("Behavioral");

        Candidate candidate = new Candidate();
        candidate.setId(11L);
        candidate.setUser(new User("Test Candidate", "candidate@example.com", "pwd", com.nucleusteq.interviewtracker.enums.UserRole.CANDIDATE));
        candidate.setCurrentStage(InterviewStage.L2_TECHNICAL);
        JobDescription jd = new JobDescription();
        jd.setJobTitle("Backend Dev");
        candidate.setJobDescription(jd);

        Interview l2 = new Interview(InterviewStage.L2_TECHNICAL, LocalDate.now().minusDays(1), LocalTime.of(10, 0), "", candidate);
        l2.setCompleted(true);

        Interview saved = new Interview(req.getInterviewStage(), req.getInterviewDate(), req.getInterviewTime(), req.getFocusAreas(), candidate);
        saved.setId(78L);

        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.findByCandidate(candidate)).thenReturn(List.of(l2));
        when(interviewRepository.findByCandidateAndInterviewStageAndIsCompletedFalse(candidate, InterviewStage.HR_ROUND)).thenReturn(Optional.empty());
        when(interviewRepository.existsByInterviewDateAndInterviewTime(req.getInterviewDate(), req.getInterviewTime())).thenReturn(false);
        when(interviewRepository.save(any(Interview.class))).thenReturn(saved);
        when(interviewRepository.findById(78L)).thenReturn(Optional.of(saved));
        when(interviewMapper.mapToResponseDto(any())).thenReturn(new InterviewResponseDto());

        InterviewResponseDto response = interviewService.scheduleInterview(req);

        assertNotNull(response);
        verifyNoInteractions(panelMemberRepository);
    }

    @Test
    void scheduleInterview_shouldThrow_whenL2RequestedWithoutCompletedL1() {
        InterviewRequestDto req = new InterviewRequestDto();
        req.setCandidateId(1L);
        req.setInterviewStage(InterviewStage.L2_TECHNICAL);
        req.setInterviewDate(LocalDate.now().plusDays(1));
        req.setInterviewTime(LocalTime.of(10, 0));
        req.setPanelMemberIds(List.of(1L));

        Candidate candidate = new Candidate();
        candidate.setId(1L);
        candidate.setUser(new User("Test Candidate", "candidate@example.com", "pwd", com.nucleusteq.interviewtracker.enums.UserRole.CANDIDATE));
        candidate.setCurrentStage(InterviewStage.SCREENING);

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.findByCandidate(candidate)).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> interviewService.scheduleInterview(req));
    }

    @Test
    void scheduleInterview_shouldThrow_whenHrRequestedWithoutCompletedL2() {
        InterviewRequestDto req = new InterviewRequestDto();
        req.setCandidateId(1L);
        req.setInterviewStage(InterviewStage.HR_ROUND);
        req.setInterviewDate(LocalDate.now().plusDays(1));
        req.setInterviewTime(LocalTime.of(10, 0));

        Candidate candidate = new Candidate();
        candidate.setId(1L);
        candidate.setUser(new User("Test Candidate", "candidate@example.com", "pwd", com.nucleusteq.interviewtracker.enums.UserRole.CANDIDATE));
        candidate.setCurrentStage(InterviewStage.L2_TECHNICAL);

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.findByCandidate(candidate)).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> interviewService.scheduleInterview(req));
    }

    @Test
    void getInterviewsForPanel_shouldFilterOutReviewedInterviews() {
        User panelUser = new User("Panel", "panel@example.com", "pwd", com.nucleusteq.interviewtracker.enums.UserRole.PANEL);
        panelUser.setActive(true);
        PanelMember panel = new PanelMember();
        panel.setUser(panelUser);

        Interview openInterview = new Interview();
        openInterview.setId(1L);
        Interview reviewedInterview = new Interview();
        reviewedInterview.setId(2L);

        InterviewPanel openAssignment = new InterviewPanel();
        openAssignment.setInterview(openInterview);
        InterviewPanel reviewedAssignment = new InterviewPanel();
        reviewedAssignment.setInterview(reviewedInterview);

        when(userRepository.findByEmail("panel@example.com")).thenReturn(Optional.of(panelUser));
        when(panelMemberRepository.findByUser(panelUser)).thenReturn(Optional.of(panel));
        when(interviewPanelRepository.findByPanelMember(panel)).thenReturn(List.of(openAssignment, reviewedAssignment));
        when(feedbackRepository.existsByInterviewAndPanelMember(openInterview, panel)).thenReturn(false);
        when(feedbackRepository.existsByInterviewAndPanelMember(reviewedInterview, panel)).thenReturn(true);
        when(interviewMapper.mapToResponseDto(openInterview)).thenReturn(new InterviewResponseDto());

        List<InterviewResponseDto> result = interviewService.getInterviewsForPanel("panel@example.com");

        assertEquals(1, result.size());
    }

    @Test
    void getInterviewsForPanel_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> interviewService.getInterviewsForPanel("missing@example.com"));
    }

    @Test
    void getInterviewsForPanel_shouldThrow_whenPanelMemberNotFound() {
        User user = new User("Panel", "panel@example.com", "pwd", com.nucleusteq.interviewtracker.enums.UserRole.PANEL);
        when(userRepository.findByEmail("panel@example.com")).thenReturn(Optional.of(user));
        when(panelMemberRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> interviewService.getInterviewsForPanel("panel@example.com"));
    }

    @Test
    void submitFeedback_shouldRejectCandidateAndCompleteInterview_whenAllPanelsReviewed() {
        FeedbackRequestDto feedbackRequest = new FeedbackRequestDto();
        feedbackRequest.setRating(1);
        feedbackRequest.setComments("Not good");
        feedbackRequest.setDecision("REJECTED");

        Candidate candidate = new Candidate();
        candidate.setUser(new User("Cand", "cand@example.com", "pwd", com.nucleusteq.interviewtracker.enums.UserRole.CANDIDATE));
        candidate.setJobDescription(new JobDescription());

        User panelUser = new User("Panel", "panel@example.com", "pwd", com.nucleusteq.interviewtracker.enums.UserRole.PANEL);
        panelUser.setActive(true);
        PanelMember panelMember = new PanelMember();
        panelMember.setUser(panelUser);

        Interview interview = new Interview();
        interview.setId(3L);
        interview.setInterviewDate(LocalDate.now().minusDays(1));
        interview.setInterviewTime(LocalTime.of(10, 0));
        interview.setCandidate(candidate);
        InterviewPanel panelAssignmentOne = new InterviewPanel(interview, panelMember);
        InterviewPanel panelAssignmentTwo = new InterviewPanel(interview, panelMember);
        interview.setInterviewPanels(List.of(panelAssignmentOne, panelAssignmentTwo));

        when(interviewRepository.findById(3L)).thenReturn(Optional.of(interview));
        when(userRepository.findByEmail("panel@example.com")).thenReturn(Optional.of(panelUser));
        when(panelMemberRepository.findByUser(panelUser)).thenReturn(Optional.of(panelMember));
        when(feedbackRepository.findByInterview(interview)).thenReturn(List.of(new Feedback(), new Feedback()));

        interviewService.submitFeedback(3L, feedbackRequest, "panel@example.com");

        assertTrue(interview.isCompleted());
        verify(interviewRepository).save(interview);
        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    void deleteInterviewPanelsByMember_shouldDeleteAllAssignments() {
        InterviewPanel a = new InterviewPanel();
        InterviewPanel b = new InterviewPanel();
        User panelUser = new User("Panel", "panel@example.com", "pwd", com.nucleusteq.interviewtracker.enums.UserRole.PANEL);
        panelUser.setActive(true);
        PanelMember panelMember = new PanelMember();
        panelMember.setUser(panelUser);
        when(interviewPanelRepository.findByPanelMember(panelMember)).thenReturn(List.of(a, b));

        interviewService.deleteInterviewPanelsByMember(panelMember);

        verify(interviewPanelRepository).delete(a);
        verify(interviewPanelRepository).delete(b);
    }

    @Test
    void submitHrFeedback_marksSelected_and_sendsResultEmail() {
        // Setup interview in past
        Candidate candidate = new Candidate();
        User user = new User();
        user.setFullName("Cand");
        user.setEmail("cand@example.com");
        JobDescription jd = new JobDescription();
        jd.setJobTitle("Dev");
        candidate.setUser(user);
        candidate.setJobDescription(jd);

        Interview interview = new Interview(InterviewStage.HR_ROUND, LocalDate.now().minusDays(1), LocalTime.of(9,0), "", candidate);
        interview.setId(200L);
        when(interviewRepository.findById(200L)).thenReturn(Optional.of(interview));

        FeedbackRequestDto req = new FeedbackRequestDto();
        req.setDecision("selected");

        when(feedbackRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Execute
        interviewService.submitHrFeedback(200L, req);

        // Candidate should be SELECTED and email sent
        assertEquals(com.nucleusteq.interviewtracker.enums.InterviewStage.SELECTED, candidate.getCurrentStage());
        verify(mailSender, atLeastOnce()).send(any(SimpleMailMessage.class));
    }

    @Test
    void submitHrFeedback_marksRejected_and_sendsResultEmail() {
        Candidate candidate = new Candidate();
        User user = new User();
        user.setFullName("Cand");
        user.setEmail("cand2@example.com");
        JobDescription jd = new JobDescription();
        jd.setJobTitle("Dev");
        candidate.setUser(user);
        candidate.setJobDescription(jd);

        Interview interview = new Interview(InterviewStage.HR_ROUND, LocalDate.now().minusDays(1), LocalTime.of(9,0), "", candidate);
        interview.setId(201L);
        when(interviewRepository.findById(201L)).thenReturn(Optional.of(interview));

        FeedbackRequestDto req = new FeedbackRequestDto();
        req.setDecision("reject");

        when(feedbackRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Execute
        interviewService.submitHrFeedback(201L, req);

        // Candidate should be REJECTED and email sent
        assertEquals(com.nucleusteq.interviewtracker.enums.InterviewStage.REJECTED, candidate.getCurrentStage());
        verify(mailSender, atLeastOnce()).send(any(SimpleMailMessage.class));
    }
}
