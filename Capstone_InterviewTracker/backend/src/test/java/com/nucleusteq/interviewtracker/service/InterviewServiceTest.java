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
import org.springframework.mail.SimpleMailMessage;

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
        testPanelMember.setUser(new User("Panel", "panel@example.com", "pass", UserRole.PANEL));
        testPanelMember.setActive(true);

        testInterview = new Interview();
        testInterview.setId(1L);
        testInterview.setCandidate(testCandidate);
        testInterview.setInterviewStage(InterviewStage.L1_TECHNICAL);
        testInterview.setInterviewDate(LocalDate.now().plusDays(1));
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

    @Test
    void getAllInterviews_shouldReturnAllInterviews() {
        Interview interview2 = new Interview();
        interview2.setId(2L);
        interview2.setCandidate(testCandidate);

        when(interviewRepository.findAll()).thenReturn(List.of(testInterview, interview2));
        when(interviewMapper.mapToResponseDto(any())).thenReturn(new InterviewResponseDto());

        List<InterviewResponseDto> result = interviewService.getAllInterviews();

        assertEquals(2, result.size());
        verify(interviewRepository).findAll();
    }

    @Test
    void getAllInterviews_shouldReturnEmptyList_whenNoInterviews() {
        when(interviewRepository.findAll()).thenReturn(Collections.emptyList());

        List<InterviewResponseDto> result = interviewService.getAllInterviews();

        assertTrue(result.isEmpty());
        verify(interviewRepository).findAll();
    }

    @Test
    void scheduleInterview_shouldSchedule_whenValidRequest() {
        testCandidate.setCurrentStage(InterviewStage.SCREENING);

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(testCandidate));
        when(panelMemberRepository.findById(1L)).thenReturn(Optional.of(testPanelMember));
        when(interviewRepository.save(any())).thenReturn(testInterview);
        when(interviewRepository.findById(testInterview.getId())).thenReturn(Optional.of(testInterview));
        when(interviewPanelRepository.save(any())).thenReturn(new InterviewPanel());
        when(interviewMapper.mapToResponseDto(any())).thenReturn(new InterviewResponseDto());

        InterviewResponseDto result = interviewService.scheduleInterview(requestDto);

        assertNotNull(result);
        verify(interviewRepository).save(any(Interview.class));
        verify(candidateRepository).save(testCandidate);
        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }

    @Test
    void scheduleInterview_shouldThrow_whenCandidateNotFound() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> interviewService.scheduleInterview(requestDto));
    }

    @Test
    void scheduleInterview_shouldThrow_whenPanelMemberNotFound() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(testCandidate));
        when(panelMemberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> interviewService.scheduleInterview(requestDto));
    }

    @Test
    void getInterviewsForPanel_shouldReturnInterviews_whenValidEmail() {
        InterviewPanel panel = new InterviewPanel();
        panel.setPanelMember(testPanelMember);
        panel.setInterview(testInterview);
        testInterview.setInterviewPanels(List.of(panel));

        when(userRepository.findByEmail("panel@example.com")).thenReturn(Optional.of(testPanelMember.getUser()));
        when(panelMemberRepository.findByUser(testPanelMember.getUser())).thenReturn(Optional.of(testPanelMember));
        when(interviewPanelRepository.findByPanelMember(testPanelMember)).thenReturn(List.of(panel));
        when(interviewMapper.mapToResponseDto(any())).thenReturn(new InterviewResponseDto());

        List<InterviewResponseDto> result = interviewService.getInterviewsForPanel("panel@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getInterviewsForPanel_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> interviewService.getInterviewsForPanel("unknown@example.com"));
    }

    @Test
    void submitFeedback_shouldNotComplete_whenNotAllPanelsSubmitted() {
        FeedbackRequestDto feedbackRequest = new FeedbackRequestDto();
        feedbackRequest.setRating(4);
        feedbackRequest.setDecision("SELECTED");

        PanelMember panel2 = new PanelMember();
        panel2.setId(2L);

        InterviewPanel panelAssignment1 = new InterviewPanel();
        panelAssignment1.setPanelMember(testPanelMember);

        InterviewPanel panelAssignment2 = new InterviewPanel();
        panelAssignment2.setPanelMember(panel2);

        testInterview.setInterviewPanels(List.of(panelAssignment1, panelAssignment2));

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(testInterview));
        when(userRepository.findByEmail("panel@example.com")).thenReturn(Optional.of(testPanelMember.getUser()));
        when(panelMemberRepository.findByUser(testPanelMember.getUser())).thenReturn(Optional.of(testPanelMember));
        when(feedbackRepository.findByInterview(testInterview)).thenReturn(List.of());

        interviewService.submitFeedback(1L, feedbackRequest, "panel@example.com");

        assertFalse(testInterview.isCompleted());
        verify(feedbackRepository).save(any(Feedback.class));
        verify(interviewRepository, never()).save(testInterview);
    }

    @Test
    void submitFeedback_shouldReject_whenDecisionIsRejected() {
        FeedbackRequestDto feedbackRequest = new FeedbackRequestDto();
        feedbackRequest.setRating(2);
        feedbackRequest.setDecision("REJECTED");

        InterviewPanel panelAssignment = new InterviewPanel();
        panelAssignment.setPanelMember(testPanelMember);
        testInterview.setInterviewPanels(List.of(panelAssignment));

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(testInterview));
        when(userRepository.findByEmail("panel@example.com")).thenReturn(Optional.of(testPanelMember.getUser()));
        when(panelMemberRepository.findByUser(testPanelMember.getUser())).thenReturn(Optional.of(testPanelMember));
        when(feedbackRepository.findByInterview(testInterview)).thenReturn(List.of(new Feedback("Not suitable","","","",2, FeedbackStatus.REJECTED, testInterview, testPanelMember)));
        when(interviewRepository.save(any())).thenReturn(testInterview);

        interviewService.submitFeedback(1L, feedbackRequest, "panel@example.com");

        assertTrue(testInterview.isCompleted());
        assertEquals(InterviewStage.REJECTED, testCandidate.getCurrentStage());
        verify(candidateRepository).save(testCandidate);
    }

    @Test
    void getInterviewsForCandidate_shouldReturnEmptyList_whenNoInterviews() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(candidateRepository.findByUser(testUser)).thenReturn(Optional.of(testCandidate));
        when(interviewRepository.findByCandidate(testCandidate)).thenReturn(Collections.emptyList());

        List<InterviewResponseDto> result = interviewService.getInterviewsForCandidate("test@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void submitFeedback_shouldThrow_whenPanelMemberNotFound() {
        FeedbackRequestDto feedbackRequest = new FeedbackRequestDto();

        when(interviewRepository.findById(1L)).thenReturn(Optional.of(testInterview));
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> interviewService.submitFeedback(1L, feedbackRequest, "unknown@example.com"));
    }

    @Test
    void getInterviewsForPanel_shouldThrow_whenPanelMemberNotFound() {
        when(userRepository.findByEmail("panel@example.com")).thenReturn(Optional.of(testPanelMember.getUser()));
        when(panelMemberRepository.findByUser(testPanelMember.getUser())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> interviewService.getInterviewsForPanel("panel@example.com"));
    }
}
