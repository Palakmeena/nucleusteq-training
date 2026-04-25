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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for interview scheduling business logic.
 * Handles scheduling, panel assignment and interview retrieval.
 */
@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final CandidateRepository candidateRepository;
    private final PanelMemberRepository panelMemberRepository;
    private final InterviewPanelRepository interviewPanelRepository;
    private final UserRepository userRepository;
    private final InterviewMapper interviewMapper;

    /**
     * Constructor injection for all required dependencies.
     *
     * @param interviewRepository      for interview DB operations
     * @param candidateRepository      for fetching candidate entity
     * @param panelMemberRepository    for fetching panel member entities
     * @param interviewPanelRepository for panel assignment operations
     * @param userRepository           for loading panel member by email
     * @param interviewMapper          for entity to DTO conversions
     */
    @Autowired
    public InterviewService(final InterviewRepository interviewRepository,
            final CandidateRepository candidateRepository,
            final PanelMemberRepository panelMemberRepository,
            final InterviewPanelRepository interviewPanelRepository,
            final UserRepository userRepository,
            final InterviewMapper interviewMapper) {
        this.interviewRepository = interviewRepository;
        this.candidateRepository = candidateRepository;
        this.panelMemberRepository = panelMemberRepository;
        this.interviewPanelRepository = interviewPanelRepository;
        this.userRepository = userRepository;
        this.interviewMapper = interviewMapper;
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

        Interview interview = new Interview(
                request.getInterviewStage(),
                request.getInterviewDate(),
                request.getInterviewTime(),
                request.getFocusAreas(),
                candidate);

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

        return interviewMapper.mapToResponseDto(reloaded);
    }

    /**
     * Returns all interviews scheduled for a specific candidate.
     * HR uses this to see the full interview history of a candidate.
     *
     * @param candidateId the ID of the candidate
     * @return list of all interviews for this candidate
     * @throws jakarta.persistence.EntityNotFoundException if candidate not found
     */
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
    public List<InterviewResponseDto> getInterviewsForPanel(final String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "User not found with email: " + email));

        PanelMember panelMember = panelMemberRepository.findByUser(user)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Panel member not found for this user"));

        /*
         * Get all InterviewPanel assignments for this panel member,
         * extract the Interview from each, and map to response DTOs.
         */
        return interviewPanelRepository.findByPanelMember(panelMember)
                .stream()
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
                .findByCandidateAndInterviewStage(candidate, stage)
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
}