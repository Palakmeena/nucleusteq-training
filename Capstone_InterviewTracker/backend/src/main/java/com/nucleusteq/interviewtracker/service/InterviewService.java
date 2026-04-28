package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.dto.InterviewRequestDto;
import com.nucleusteq.interviewtracker.dto.InterviewResponseDto;
import com.nucleusteq.interviewtracker.entity.Candidate;
import com.nucleusteq.interviewtracker.entity.Interview;
import com.nucleusteq.interviewtracker.entity.InterviewPanel;
import com.nucleusteq.interviewtracker.entity.PanelMember;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.enums.InterviewStage;
import com.nucleusteq.interviewtracker.mapper.InterviewMapper;
import com.nucleusteq.interviewtracker.repository.CandidateRepository;
import com.nucleusteq.interviewtracker.repository.InterviewPanelRepository;
import com.nucleusteq.interviewtracker.repository.InterviewRepository;
import com.nucleusteq.interviewtracker.repository.PanelMemberRepository;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import com.nucleusteq.interviewtracker.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nucleusteq.interviewtracker.dto.FeedbackRequestDto;
import com.nucleusteq.interviewtracker.entity.Feedback;
import com.nucleusteq.interviewtracker.enums.FeedbackStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for interview scheduling business logic.
 * Handles scheduling, panel assignment and interview retrieval.
 */
@Service
public class InterviewService {

    private static final Logger logger = LoggerFactory.getLogger(InterviewService.class);

    private final InterviewRepository interviewRepository;
    private final CandidateRepository candidateRepository;
    private final PanelMemberRepository panelMemberRepository;
    private final InterviewPanelRepository interviewPanelRepository;
    private final UserRepository userRepository;
    private final InterviewMapper interviewMapper;
    private final FeedbackRepository feedbackRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Autowired
    public InterviewService(final InterviewRepository interviewRepository,
            final CandidateRepository candidateRepository,
            final PanelMemberRepository panelMemberRepository,
            final InterviewPanelRepository interviewPanelRepository,
            final UserRepository userRepository,
            final InterviewMapper interviewMapper,
            final FeedbackRepository feedbackRepository,
            final JavaMailSender mailSender) {
        this.interviewRepository = interviewRepository;
        this.candidateRepository = candidateRepository;
        this.panelMemberRepository = panelMemberRepository;
        this.interviewPanelRepository = interviewPanelRepository;
        this.userRepository = userRepository;
        this.interviewMapper = interviewMapper;
        this.feedbackRepository = feedbackRepository;
        this.mailSender = mailSender;
    }

    /**
     * Schedules a new interview for a candidate at a specific stage.
     * Validates candidate exists, stage is valid for scheduling,
     * no duplicate interview exists for this stage, and panel
     * members are active before creating the interview.
     *
     * @param request the interview scheduling details from HR
     * @return the scheduled interview as a response DTO
     * @throws IllegalArgumentException                    if any validation fails
     * @throws jakarta.persistence.EntityNotFoundException if entities not found
     */
    @Transactional
    public InterviewResponseDto scheduleInterview(
            final InterviewRequestDto request) {

        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Candidate not found with id: " + request.getCandidateId()));

        validateInterviewStage(request.getInterviewStage());
        validateNoDuplicateInterview(candidate, request.getInterviewStage());
        validateSequentialWorkflow(candidate, request.getInterviewStage());

        Interview interview = new Interview(
                request.getInterviewStage(),
                request.getInterviewDate(),
                request.getInterviewTime(),
                request.getFocusAreas(),
                candidate);
        
        interview.setMeetingLink(request.getMeetingLink());

        Interview savedInterview = interviewRepository.save(interview);

        /*
         * Fetch each panel member by ID, validate they are active,
         * and create an InterviewPanel assignment for each one.
         * Max 2 panel members enforced by DTO validation already
         * but we double check here for safety.
         */
        for (Long panelMemberId : request.getPanelMemberIds()) {
            PanelMember panelMember = panelMemberRepository
                    .findById(panelMemberId)
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                            "Panel member not found with id: " + panelMemberId));

            if (!panelMember.isActive()) {
                throw new IllegalArgumentException(
                        "Panel member " + panelMember.getFullName()
                                + " has not activated their account yet");
            }

            InterviewPanel assignment = new InterviewPanel(savedInterview, panelMember);
            interviewPanelRepository.save(assignment);
        }

        /*
         * Reload the interview with its panels so the mapper
         * can extract panel names and IDs for the response.
         */
        Interview reloaded = interviewRepository.findById(savedInterview.getId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Interview not found after saving — this should never happen"));

        // Send email notification to candidate
        sendInterviewScheduledEmail(candidate, request);

        return interviewMapper.mapToResponseDto(reloaded);
    }

    /**
     * Sends a simple email to the candidate informing them about
     * their scheduled interview. Only essential details are shared.
     */
    private void sendInterviewScheduledEmail(final Candidate candidate, final InterviewRequestDto request) {
        try {
            String candidateEmail = candidate.getEmail();
            String candidateName = candidate.getFullName();
            String stageName = formatStageName(request.getInterviewStage());
            String date = request.getInterviewDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            String time = request.getInterviewTime().format(DateTimeFormatter.ofPattern("hh:mm a"));

            SimpleMailMessage message = new SimpleMailMessage();
            if (fromEmail != null && !fromEmail.isBlank()) {
                message.setFrom(fromEmail);
            }
            message.setTo(candidateEmail);
            message.setSubject("Your " + stageName + " Interview has been Scheduled - HireTrack");
            message.setText(
                    "Hi " + candidateName + ",\n\n"
                    + "Your " + stageName + " interview has been scheduled.\n\n"
                    + "Date: " + date + "\n"
                    + "Time: " + time + "\n\n"
                    + "Please be prepared and ensure you are available at the scheduled time.\n\n"
                    + "Best of luck!\n"
                    + "HireTrack Team"
            );

            mailSender.send(message);
            logger.info("Interview scheduled email sent to {}", candidateEmail);
        } catch (Exception ex) {
            logger.warn("Failed to send interview schedule email to {}: {}", candidate.getEmail(), ex.getMessage());
        }
    }

    /**
     * Converts InterviewStage enum to a human-readable name for email.
     */
    private String formatStageName(final InterviewStage stage) {
        switch (stage) {
            case L1_TECHNICAL: return "L1 Technical";
            case L2_TECHNICAL: return "L2 Technical";
            case HR_ROUND: return "HR Round";
            default: return stage.name();
        }
    }

    /**
     * Returns all interviews scheduled for a specific candidate.
     * HR uses this to see the full interview history of a candidate.
     *
     * @param candidateId the ID of the candidate
     * @return list of all interviews for this candidate
     * @throws jakarta.persistence.EntityNotFoundException if candidate not found
     */
    @Transactional(readOnly = true)
    public List<InterviewResponseDto> getInterviewsByCandidate(
            final Long candidateId) {

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Candidate not found with id: " + candidateId));

        return interviewRepository.findByCandidate(candidate)
                .stream()
                .map(interviewMapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Returns all interviews assigned to the logged-in panel member.
     * Panel member can only see their own assigned interviews.
     *
     * @param email the email of the logged-in panel member from JWT
     * @return list of interviews assigned to this panel member
     * @throws jakarta.persistence.EntityNotFoundException if not found
     */
    @Transactional(readOnly = true)
    public List<InterviewResponseDto> getInterviewsForPanel(final String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "User not found with email: " + email));

        PanelMember panelMember = panelMemberRepository.findByUser(user)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Panel member not found for this user"));

        /*
         * Get all InterviewPanel assignments for this panel member.
         * Filter out interviews where this specific panel member has already submitted feedback.
         */
        return interviewPanelRepository.findByPanelMember(panelMember)
                .stream()
                .filter(assignment -> {
                    Interview interview = assignment.getInterview();
                    // Check if feedback exists for this (interview, panelMember) pair
                    return !feedbackRepository.existsByInterviewAndPanelMember(interview, panelMember);
                })
                .map(InterviewPanel::getInterview)
                .map(interviewMapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Returns a single interview by its ID.
     * Used by HR to view full details of a specific interview.
     *
     * @param id the interview's database ID
     * @return the interview as a response DTO
     * @throws jakarta.persistence.EntityNotFoundException if not found
     */
    @Transactional(readOnly = true)
    public InterviewResponseDto getInterviewById(final Long id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Interview not found with id: " + id));
        return interviewMapper.mapToResponseDto(interview);
    }

    /**
     * Validates that the interview stage is schedulable.
     * Only L1_TECHNICAL, L2_TECHNICAL and HR_ROUND can be scheduled.
     * PROFILING and SCREENING are not interview stages.
     *
     * @param stage the stage to validate
     * @throws IllegalArgumentException if stage cannot be scheduled
     */
    private void validateInterviewStage(final InterviewStage stage) {
        if (stage == InterviewStage.PROFILING
                || stage == InterviewStage.SCREENING
                || stage == InterviewStage.SELECTED
                || stage == InterviewStage.REJECTED) {
            throw new IllegalArgumentException(
                    "Cannot schedule interview for stage: " + stage
                            + ". Only L1_TECHNICAL, L2_TECHNICAL and HR_ROUND are valid.");
        }
    }

    /**
     * Checks that no interview already exists for this candidate at this stage.
     * Prevents HR from accidentally scheduling duplicate interviews.
     *
     * @param candidate the candidate entity
     * @param stage     the interview stage to check
     * @throws IllegalArgumentException if interview already exists
     */
    private void validateNoDuplicateInterview(final Candidate candidate,
            final InterviewStage stage) {
        interviewRepository
                .findByCandidateAndInterviewStageAndIsCompletedFalse(candidate, stage)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "An interview for stage " + stage
                                    + " already exists for this candidate");
                });
    }

    /**
     * Returns all interviews for the logged-in candidate.
     * Candidate can only see their own interview schedule.
     *
     * @param email the email of the logged-in candidate from JWT
     * @return list of interviews for this candidate
     * @throws jakarta.persistence.EntityNotFoundException if not found
     */
    @Transactional(readOnly = true)
    public List<InterviewResponseDto> getInterviewsForCandidate(final String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "User not found with email: " + email));

        Candidate candidate = candidateRepository.findByUser(user)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Candidate profile not found for this user"));

        return interviewRepository.findByCandidate(candidate)
                .stream()
                .map(interviewMapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private void validateSequentialWorkflow(final Candidate candidate, final InterviewStage requestedStage) {
        if (requestedStage == InterviewStage.L1_TECHNICAL) {
            if (candidate.getCurrentStage() == InterviewStage.PROFILING) {
                throw new IllegalArgumentException("Cannot schedule L1 Technical interview. Candidate must be moved to SCREENING stage first.");
            }
        }
        if (requestedStage == InterviewStage.L2_TECHNICAL) {
            // Check if ANY L1 interview for this candidate is completed
            boolean l1Completed = interviewRepository.findByCandidate(candidate)
                    .stream()
                    .filter(i -> i.getInterviewStage() == InterviewStage.L1_TECHNICAL)
                    .anyMatch(Interview::isCompleted);
            if (!l1Completed) {
                throw new IllegalArgumentException("Cannot schedule L2 Technical interview. L1 Technical interview must be completed first.");
            }
        }
        if (requestedStage == InterviewStage.HR_ROUND) {
            boolean l2Completed = interviewRepository.findByCandidate(candidate)
                    .stream()
                    .filter(i -> i.getInterviewStage() == InterviewStage.L2_TECHNICAL)
                    .anyMatch(Interview::isCompleted);
            if (!l2Completed) {
                throw new IllegalArgumentException("Cannot schedule HR Round. L2 Technical interview must be completed first.");
            }
        }
    }

    @Transactional
    public void submitFeedback(Long interviewId, FeedbackRequestDto request, String panelEmail) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Interview not found"));

        User user = userRepository.findByEmail(panelEmail)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found"));

        PanelMember panelMember = panelMemberRepository.findByUser(user)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Panel member not found"));

        FeedbackStatus status = (request.getDecision() != null && request.getDecision().toUpperCase().contains("REJECT")) 
                                ? FeedbackStatus.REJECTED : FeedbackStatus.SELECTED;

        Feedback feedback = new Feedback(
                request.getComments(),
                request.getStrengths(),
                request.getWeaknesses(),
                "Overall Assessment", // areasCovered
                request.getRating(),
                status,
                interview,
                panelMember
        );

        feedbackRepository.save(feedback);

        // Check if all panels have submitted feedback
        List<Feedback> allFeedbacks = feedbackRepository.findByInterview(interview);
        long assignedCount = interview.getInterviewPanels().size();

        if (allFeedbacks.size() >= assignedCount) {
            interview.setCompleted(true);
            interviewRepository.save(interview);
        }
    }
}