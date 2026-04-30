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

        /*
         * Panel members are required for L1 and L2 interviews.
         * HR Round is conducted by HR themselves — no panel needed.
         */
        List<Long> panelIds = request.getPanelMemberIds() != null ? request.getPanelMemberIds() : java.util.Collections.emptyList();
        if (request.getInterviewStage() != InterviewStage.HR_ROUND) {
            if (panelIds.isEmpty()) {
                throw new IllegalArgumentException("At least one panel member is required for " + request.getInterviewStage());
            }
            if (panelIds.size() > 2) {
                throw new IllegalArgumentException("Maximum 2 panel members allowed per interview");
            }
        }

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
         * For HR_ROUND, this list is empty so the loop is skipped.
         */
        for (Long panelMemberId : panelIds) {
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

        /*
         * Notify selected panel members (if any). Each panel member gets their
         * own email containing candidate details, JD, interview time and focus areas.
         * Failures here are non-fatal and are logged per recipient.
         */
        List<Long> panelIds = request.getPanelMemberIds() != null ? request.getPanelMemberIds() : java.util.Collections.emptyList();
        if (!panelIds.isEmpty()) {
            for (Long panelId : panelIds) {
                try {
                    PanelMember panel = panelMemberRepository.findById(panelId)
                            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Panel member not found with id: " + panelId));

                    String panelEmail = panel.getEmail();
                    String panelName = panel.getFullName();

                    SimpleMailMessage pm = new SimpleMailMessage();
                    if (fromEmail != null && !fromEmail.isBlank()) {
                        pm.setFrom(fromEmail);
                    }
                    pm.setTo(panelEmail);
                    pm.setSubject("Interview Assignment: " + candidate.getFullName() + " - " + candidate.getJobDescription().getJobTitle());

                    StringBuilder body = new StringBuilder();
                    body.append("Hi ").append(panelName).append(",\n\n");
                    body.append("You have been assigned as a panel member for the following interview:\n\n");
                    body.append("Candidate: ").append(candidate.getFullName()).append(" (" ).append(candidate.getEmail()).append(")\n");
                    body.append("Job: ").append(candidate.getJobDescription().getJobTitle()).append("\n");
                    body.append("Stage: ").append(formatStageName(request.getInterviewStage())).append("\n");
                    body.append("Date: ").append(request.getInterviewDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))).append("\n");
                    body.append("Time: ").append(request.getInterviewTime().format(DateTimeFormatter.ofPattern("hh:mm a"))).append("\n");
                    if (request.getMeetingLink() != null && !request.getMeetingLink().isBlank()) {
                        body.append("Meeting Link: ").append(request.getMeetingLink()).append("\n");
                    }
                    if (request.getFocusAreas() != null && !request.getFocusAreas().isBlank()) {
                        body.append("Focus Areas: ").append(request.getFocusAreas()).append("\n");
                    }
                    body.append("\nPlease be prepared to evaluate the candidate on the focus areas mentioned.\n\n");
                    body.append("Best Regards,\nHireTrack Team");

                    pm.setText(body.toString());
                    mailSender.send(pm);
                    logger.info("Interview assignment email sent to panel member {} <{}>", panelName, panelEmail);
                } catch (Exception pex) {
                    logger.warn("Failed to send interview assignment email to panel id {}: {}", panelId, pex.getMessage());
                }
            }
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

            // If any feedback was a rejection, the candidate is rejected
            boolean isRejected = allFeedbacks.stream()
                    .anyMatch(f -> f.getFeedbackStatus() == FeedbackStatus.REJECTED);

            if (isRejected) {
                Candidate candidate = interview.getCandidate();
                candidate.setCurrentStage(InterviewStage.REJECTED);
                candidateRepository.save(candidate);
                sendResultEmail(candidate, false);
            }
        }
    }
    /**
     * Submits HR feedback for an HR Round interview.
     * HR conducts the interview themselves, so no panel member is involved.
     * The interview is marked as completed immediately after feedback.
     *
     * @param interviewId the interview ID
     * @param request     the feedback details
     * @throws IllegalArgumentException if the interview is not an HR Round
     */
    @Transactional
    public void submitHrFeedback(Long interviewId, FeedbackRequestDto request) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Interview not found"));

        if (interview.getInterviewStage() != InterviewStage.HR_ROUND) {
            throw new IllegalArgumentException("HR feedback can only be submitted for HR Round interviews");
        }

        if (interview.isCompleted()) {
            throw new IllegalArgumentException("Feedback has already been submitted for this interview");
        }

        FeedbackStatus status = (request.getDecision() != null && request.getDecision().toUpperCase().contains("REJECT"))
                                ? FeedbackStatus.REJECTED : FeedbackStatus.SELECTED;

        Feedback feedback = new Feedback(
                request.getComments(),
                request.getStrengths(),
                request.getWeaknesses(),
                "HR Round Assessment",
                request.getRating(),
                status,
                interview,
                null  // No panel member for HR Round
        );

        feedbackRepository.save(feedback);

        // HR is the only interviewer, so mark completed immediately
        interview.setCompleted(true);
        interviewRepository.save(interview);

        // Update candidate status based on HR decision
        Candidate candidate = interview.getCandidate();
        if (status == FeedbackStatus.REJECTED) {
            candidate.setCurrentStage(InterviewStage.REJECTED);
            candidateRepository.save(candidate);
            sendResultEmail(candidate, false);
        } else {
            candidate.setCurrentStage(InterviewStage.SELECTED);
            candidateRepository.save(candidate);
            sendResultEmail(candidate, true);
        }
    }
    /**
     * Sends an email to the candidate regarding their selection or rejection.
     * 
     * @param candidate  the candidate entity
     * @param isSelected true if selected, false if rejected
     */
    private void sendResultEmail(final Candidate candidate, final boolean isSelected) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (fromEmail != null && !fromEmail.isBlank()) {
                message.setFrom(fromEmail);
            }
            message.setTo(candidate.getEmail());
            
            if (isSelected) {
                message.setSubject("Congratulations! You are selected for " + candidate.getJobDescription().getJobTitle() + " - HireTrack");
                message.setText(
                    "Hi " + candidate.getFullName() + ",\n\n"
                    + "We are pleased to inform you that you have been selected for the position of " 
                    + candidate.getJobDescription().getJobTitle() + " at our organization.\n\n"
                    + "Our HR team will get in touch with you shortly regarding the next steps and offer details.\n\n"
                    + "Congratulations once again!\n\n"
                    + "Best Regards,\n"
                    + "HireTrack Recruitment Team"
                );
            } else {
                message.setSubject("Update regarding your application for " + candidate.getJobDescription().getJobTitle() + " - HireTrack");
                message.setText(
                    "Hi " + candidate.getFullName() + ",\n\n"
                    + "Thank you for your interest in the " + candidate.getJobDescription().getJobTitle() 
                    + " position and for taking the time to interview with us.\n\n"
                    + "After careful consideration, we regret to inform you that we will not be moving forward with your application at this time.\n\n"
                    + "We appreciate your time and wish you the best in your future endeavors.\n\n"
                    + "Best Regards,\n"
                    + "HireTrack Recruitment Team"
                );
            }
            
            mailSender.send(message);
            logger.info("Result email sent successfully to {}", candidate.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send result email to {}: {}", candidate.getEmail(), e.getMessage());
        }
    }
}