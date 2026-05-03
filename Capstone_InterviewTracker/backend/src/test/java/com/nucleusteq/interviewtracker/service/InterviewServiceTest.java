package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.dto.FeedbackRequestDto;
import com.nucleusteq.interviewtracker.dto.InterviewRequestDto;
import com.nucleusteq.interviewtracker.dto.InterviewResponseDto;
import com.nucleusteq.interviewtracker.entity.*;
import com.nucleusteq.interviewtracker.enums.FeedbackStatus;
import com.nucleusteq.interviewtracker.enums.InterviewStage;
import com.nucleusteq.interviewtracker.enums.UserRole;
import com.nucleusteq.interviewtracker.mapper.InterviewMapper;
import com.nucleusteq.interviewtracker.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private PanelMemberRepository panelMemberRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private InterviewPanelRepository interviewPanelRepository;

    @Mock
    private InterviewMapper interviewMapper;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private InterviewService interviewService;

    private Candidate testCandidate;
    private User testUser;
    private PanelMember testPanelMember;
    private Interview testInterview;
    private InterviewRequestDto requestDto;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "test@example.com", "password", UserRole.CANDIDATE);
        testUser.setActive(true);

        testCandidate = new Candidate();
        testCandidate.setId(1L);
        testCandidate.setUser(testUser);
        testCandidate.setCurrentStage(InterviewStage.SCREENING);
        testCandidate.setFullName("Test Candidate");
        testCandidate.setEmail("candidate@example.com");

        JobDescription jd = new JobDescription();
        jd.setJobTitle("Backend Developer");
        testCandidate.setJobDescription(jd);

        testPanelMember = new PanelMember();
        testPanelMember.setId(1L);
        testPanelMember.setActive(true);
        testPanelMember.setUser(new User("Panel", "panel@example.com", "pass", UserRole.PANEL));

        testInterview = new Interview();
        testInterview.setId(1L);
        testInterview.setCandidate(testCandidate);
        testInterview.setInterviewStage(InterviewStage.L1_TECHNICAL);
        testInterview.setInterviewDate(LocalDate.now().minusDays(1));
        testInterview.setInterviewTime(LocalTime.of(10, 0));
        testInterview.setCompleted(false);

        requestDto = new InterviewRequestDto();
        requestDto.setCandidateId(1L);
        requestDto.setInterviewStage(InterviewStage.L1_TECHNICAL);
        requestDto.setInterviewDate(LocalDate.now().plusDays(1));
        requestDto.setInterviewTime(LocalTime.of(10, 0));
        requestDto.setFocusAreas("Java, Spring Boot");
        requestDto.setPanelMemberIds(List.of(1L));
    }

        @Test
        void scheduleInterview_shouldWork_whenSlotIsAvailable() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(testCandidate));
        when(interviewRepository.existsByInterviewDateAndInterviewTime(requestDto.getInterviewDate(), requestDto.getInterviewTime()))
            .thenReturn(false);
        when(interviewRepository.findByCandidateAndInterviewStageAndIsCompletedFalse(testCandidate, InterviewStage.L1_TECHNICAL))
            .thenReturn(Optional.empty());
        when(interviewRepository.save(any(Interview.class))).thenAnswer(invocation -> {
            Interview saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(testInterview));
        when(panelMemberRepository.findById(1L)).thenReturn(Optional.of(testPanelMember));
        when(interviewMapper.mapToResponseDto(any())).thenReturn(new InterviewResponseDto());

        InterviewResponseDto response = interviewService.scheduleInterview(requestDto);

        assertNotNull(response);
        verify(interviewRepository).save(any(Interview.class));
        }

    @Test
    void scheduleInterview_shouldOverwriteExistingInterview_whenRescheduledBeforeTime() {
        InterviewPanel oldAssignment = new InterviewPanel(testInterview, testPanelMember);
        testInterview.getInterviewPanels().add(oldAssignment);
        testInterview.setInterviewDate(LocalDate.now().plusDays(2));
        testInterview.setInterviewTime(LocalTime.of(10, 0));

        InterviewRequestDto rescheduleRequest = new InterviewRequestDto();
        rescheduleRequest.setCandidateId(1L);
        rescheduleRequest.setInterviewStage(InterviewStage.L1_TECHNICAL);
        rescheduleRequest.setInterviewDate(LocalDate.now().plusDays(3));
        rescheduleRequest.setInterviewTime(LocalTime.of(11, 30));
        rescheduleRequest.setFocusAreas("Revised topics");
        rescheduleRequest.setPanelMemberIds(List.of(1L));

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(testCandidate));
        when(interviewRepository.findByCandidateAndInterviewStageAndIsCompletedFalse(testCandidate, InterviewStage.L1_TECHNICAL))
                .thenReturn(Optional.of(testInterview));
        when(interviewRepository.existsByInterviewDateAndInterviewTimeAndIdNot(
                rescheduleRequest.getInterviewDate(), rescheduleRequest.getInterviewTime(), testInterview.getId()))
                .thenReturn(false);
        when(interviewRepository.save(any(Interview.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(testInterview));
        when(panelMemberRepository.findById(1L)).thenReturn(Optional.of(testPanelMember));
        when(interviewMapper.mapToResponseDto(any())).thenReturn(new InterviewResponseDto());

        InterviewResponseDto response = interviewService.scheduleInterview(rescheduleRequest);

        assertNotNull(response);
        assertEquals(1, testInterview.getInterviewPanels().size());
        assertEquals("Revised topics", testInterview.getFocusAreas());
        assertEquals(LocalDate.now().plusDays(3), testInterview.getInterviewDate());
        assertEquals(LocalTime.of(11, 30), testInterview.getInterviewTime());
        verify(interviewRepository).save(testInterview);
    }

    @Test
    void scheduleInterview_shouldThrow_whenExistingInterviewAlreadyStarted() {
        testInterview.setInterviewDate(LocalDate.now().minusDays(1));
        testInterview.setInterviewTime(LocalTime.of(10, 0));

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(testCandidate));
        when(interviewRepository.findByCandidateAndInterviewStageAndIsCompletedFalse(testCandidate, InterviewStage.L1_TECHNICAL))
                .thenReturn(Optional.of(testInterview));

        assertThrows(IllegalArgumentException.class,
                () -> interviewService.scheduleInterview(requestDto));
    }

        @Test
        void scheduleInterview_shouldThrow_whenSlotAlreadyBooked() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(testCandidate));
        when(interviewRepository.existsByInterviewDateAndInterviewTime(requestDto.getInterviewDate(), requestDto.getInterviewTime()))
            .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
            () -> interviewService.scheduleInterview(requestDto));
        }

        @Test
        void scheduleInterview_shouldThrow_whenDateTimeIsInPast() {
        InterviewRequestDto pastRequest = new InterviewRequestDto();
        pastRequest.setCandidateId(1L);
        pastRequest.setInterviewStage(InterviewStage.L1_TECHNICAL);
        pastRequest.setInterviewDate(LocalDate.now().minusDays(1));
        pastRequest.setInterviewTime(LocalTime.of(10, 0));
        pastRequest.setPanelMemberIds(List.of(1L));

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(testCandidate));

        assertThrows(IllegalArgumentException.class,
            () -> interviewService.scheduleInterview(pastRequest));
        }

    @Test
    void getInterviewById_shouldReturnInterview_whenFound() {
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(testInterview));
        when(interviewMapper.mapToResponseDto(testInterview)).thenReturn(new InterviewResponseDto());

        InterviewResponseDto result = interviewService.getInterviewById(1L);

        assertNotNull(result);
        verify(interviewRepository).findById(1L);
    }

    @Test
    void getInterviewById_shouldThrow_whenNotFound() {
        when(interviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> interviewService.getInterviewById(99L));
    }

    @Test
    void getInterviewsForCandidate_shouldReturnInterviews() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(candidateRepository.findByUser(testUser)).thenReturn(Optional.of(testCandidate));
        when(interviewRepository.findByCandidate(testCandidate)).thenReturn(List.of(testInterview));
        when(interviewMapper.mapToResponseDto(any())).thenReturn(new InterviewResponseDto());

        List<InterviewResponseDto> result = interviewService.getInterviewsForCandidate("test@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getInterviewsForCandidate_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> interviewService.getInterviewsForCandidate("unknown@example.com"));
    }

    @Test
    void getInterviewsForCandidate_shouldThrow_whenCandidateNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(candidateRepository.findByUser(testUser)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> interviewService.getInterviewsForCandidate("test@example.com"));
    }

    @Test
    void submitFeedback_shouldWork_forPanelMember() {
        FeedbackRequestDto feedbackRequest = new FeedbackRequestDto();
        feedbackRequest.setRating(4);
        feedbackRequest.setComments("Good performance");
        feedbackRequest.setDecision("SELECTED");

        InterviewPanel panelAssignment = new InterviewPanel();
        panelAssignment.setInterview(testInterview);
        panelAssignment.setPanelMember(testPanelMember);
        testInterview.setInterviewPanels(List.of(panelAssignment));

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(testInterview));
        when(userRepository.findByEmail("panel@example.com")).thenReturn(Optional.of(testPanelMember.getUser()));
        when(panelMemberRepository.findByUser(testPanelMember.getUser())).thenReturn(Optional.of(testPanelMember));
        when(feedbackRepository.findByInterview(testInterview)).thenReturn(List.of());

        interviewService.submitFeedback(1L, feedbackRequest, "panel@example.com");

        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    void submitFeedback_shouldThrow_whenInterviewHasNotStarted() {
        FeedbackRequestDto feedbackRequest = new FeedbackRequestDto();
        testInterview.setInterviewDate(LocalDate.now().plusDays(1));

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(testInterview));

        assertThrows(IllegalArgumentException.class,
                () -> interviewService.submitFeedback(1L, feedbackRequest, "panel@example.com"));
    }

    @Test
    void submitFeedback_shouldThrow_whenInterviewNotFound() {
        FeedbackRequestDto feedbackRequest = new FeedbackRequestDto();
        when(interviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> interviewService.submitFeedback(99L, feedbackRequest, "panel@example.com"));
    }

    @Test
    void submitFeedback_shouldThrow_whenUserNotFound() {
        FeedbackRequestDto feedbackRequest = new FeedbackRequestDto();
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(testInterview));
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> interviewService.submitFeedback(1L, feedbackRequest, "unknown@example.com"));
    }

    @Test
    void submitHrFeedback_shouldWork_forHRRound() {
        FeedbackRequestDto feedbackRequest = new FeedbackRequestDto();
        feedbackRequest.setRating(5);
        feedbackRequest.setComments("Excellent candidate");
        feedbackRequest.setDecision("SELECTED");

        testInterview.setInterviewStage(InterviewStage.HR_ROUND);

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(testInterview));

        interviewService.submitHrFeedback(1L, feedbackRequest);

        verify(feedbackRepository).save(any(Feedback.class));
        verify(interviewRepository).save(any(Interview.class));
        verify(candidateRepository).save(any(Candidate.class));
    }

    @Test
    void submitHrFeedback_shouldThrow_whenInterviewHasNotStarted() {
        FeedbackRequestDto feedbackRequest = new FeedbackRequestDto();
        testInterview.setInterviewStage(InterviewStage.HR_ROUND);
        testInterview.setInterviewDate(LocalDate.now().plusDays(1));

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(testInterview));

        assertThrows(IllegalArgumentException.class,
                () -> interviewService.submitHrFeedback(1L, feedbackRequest));
    }

    @Test
    void submitHrFeedback_shouldThrow_whenNotHrRound() {
        FeedbackRequestDto feedbackRequest = new FeedbackRequestDto();
        testInterview.setInterviewStage(InterviewStage.L1_TECHNICAL);

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(testInterview));

        assertThrows(IllegalArgumentException.class,
                () -> interviewService.submitHrFeedback(1L, feedbackRequest));
    }

    @Test
    void submitHrFeedback_shouldThrow_whenAlreadyCompleted() {
        FeedbackRequestDto feedbackRequest = new FeedbackRequestDto();
        testInterview.setInterviewStage(InterviewStage.HR_ROUND);
        testInterview.setCompleted(true);

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(testInterview));

        assertThrows(IllegalArgumentException.class,
                () -> interviewService.submitHrFeedback(1L, feedbackRequest));
    }

    @Test
    void submitHrFeedback_shouldRejectCandidate_whenDecisionContainsReject() {
        FeedbackRequestDto feedbackRequest = new FeedbackRequestDto();
        feedbackRequest.setRating(2);
        feedbackRequest.setComments("Not suitable");
        feedbackRequest.setDecision("REJECTED");

        testInterview.setInterviewStage(InterviewStage.HR_ROUND);

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(testInterview));

        interviewService.submitHrFeedback(1L, feedbackRequest);

        assertEquals(InterviewStage.REJECTED, testCandidate.getCurrentStage());
        verify(candidateRepository).save(testCandidate);
    }
}
