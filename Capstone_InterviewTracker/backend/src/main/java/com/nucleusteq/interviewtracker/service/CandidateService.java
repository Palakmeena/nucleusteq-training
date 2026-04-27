package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.entity.CandidateProfile;
import com.nucleusteq.interviewtracker.repository.CandidateProfileRepository;
import com.nucleusteq.interviewtracker.dto.CandidateRequestDto;
import com.nucleusteq.interviewtracker.dto.CandidateResponseDto;
import com.nucleusteq.interviewtracker.entity.Candidate;
import com.nucleusteq.interviewtracker.entity.JobDescription;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.enums.UserRole;
import com.nucleusteq.interviewtracker.mapper.CandidateMapper;
import com.nucleusteq.interviewtracker.repository.CandidateRepository;
import com.nucleusteq.interviewtracker.repository.JobDescriptionRepository;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for candidate profiling business logic.
 * Handles profile creation for both HR and self-registering candidates.
 */
@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CandidateMapper candidateMapper;
    private final CandidateProfileRepository candidateProfileRepository;

    /**
     * Constructor injection for all required dependencies.
     *
     * @param candidateRepository      for candidate DB operations
     * @param jobDescriptionRepository for fetching the applied JD
     * @param userRepository           for creating linked user account
     * @param passwordEncoder          for hashing candidate password
     * @param candidateMapper          for entity and DTO conversions
     */
    @Autowired
    public CandidateService(final CandidateRepository candidateRepository,
                            final JobDescriptionRepository jobDescriptionRepository,
                            final UserRepository userRepository,
                            final PasswordEncoder passwordEncoder,
                            final CandidateMapper candidateMapper,
                            final CandidateProfileRepository candidateProfileRepository) {
        this.candidateRepository = candidateRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.candidateMapper = candidateMapper;
        this.candidateProfileRepository = candidateProfileRepository;
    }

    /**
     * Creates a candidate profile submitted by the candidate themselves.
     * Also creates a linked User account so the candidate can log in.
     *
     * @param request the candidate profiling form data
     * @return the saved candidate as a response DTO
     * @throws IllegalArgumentException if email or mobile already exists
     */
    @Transactional
    public CandidateResponseDto createCandidateProfile(
            final CandidateRequestDto request, String authenticatedEmail) {

        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found"));

        if (!user.getEmail().equals(request.getEmail())) {
            throw new IllegalArgumentException("The email provided does not match your logged-in account email.");
        }

        // Check if the user already has an active candidate profile. 
        // A user shouldn't apply to multiple jobs at once unless rejected.
        java.util.Optional<Candidate> existingCandidateOpt = candidateRepository.findByUser(user);
        
        if (existingCandidateOpt.isPresent()) {
            Candidate existingCandidate = existingCandidateOpt.get();
            if (existingCandidate.getCurrentStage() != com.nucleusteq.interviewtracker.enums.InterviewStage.REJECTED) {
                throw new IllegalArgumentException("You already have an active application. You cannot apply to multiple jobs.");
            }
            
            // If they were rejected, allow applying again by updating their JD and resetting status
            JobDescription newJd = findActiveJdOrThrow(request.getJobDescriptionId());
            existingCandidate.setJobDescription(newJd);
            existingCandidate.setCurrentStage(com.nucleusteq.interviewtracker.enums.InterviewStage.PROFILING);
            // We could update other fields like experience, etc here from request
            // But for simplicity, we'll let candidateMapper update the entity or we update manually.
            
            Candidate saved = candidateRepository.save(existingCandidate);
            return candidateMapper.mapToResponseDto(saved);
        }

        JobDescription jd = findActiveJdOrThrow(request.getJobDescriptionId());

        Candidate candidate = candidateMapper.mapToEntity(request, jd, user);
        Candidate saved = candidateRepository.save(candidate);

        // SYNC TO LIVE PROFILE: So the candidate sees their updated info in dashboard
        syncToLiveProfile(saved, user);

        return candidateMapper.mapToResponseDto(saved);
    }

    private void syncToLiveProfile(Candidate snapshot, User user) {
        CandidateProfile profile = candidateProfileRepository.findByUser(user)
                .orElse(new CandidateProfile(user));
        
        profile.setFullName(snapshot.getFullName());
        profile.setMobileCode(snapshot.getMobileCode());
        profile.setMobileNumber(snapshot.getMobileNumber());
        profile.setDateOfBirth(snapshot.getDateOfBirth());
        profile.setResumePath(snapshot.getResumePath());
        profile.setCurrentOrganization(snapshot.getCurrentOrganization());
        profile.setTotalExperience(snapshot.getTotalExperience());
        profile.setRelevantExperience(snapshot.getRelevantExperience());
        profile.setCurrentCtc(snapshot.getCurrentCtc());
        profile.setExpectedCtc(snapshot.getExpectedCtc());
        profile.setNoticePeriod(snapshot.getNoticePeriod());
        profile.setPreferredLocation(snapshot.getPreferredLocation());
        
        candidateProfileRepository.save(profile);
    }

    /**
     * Creates a candidate profile by HR on behalf of a candidate.
     * Same logic as self-registration but called from HR endpoints.
     *
     * @param request the candidate profiling form data filled by HR
     * @return the saved candidate as a response DTO
     * @throws IllegalArgumentException if email or mobile already exists
     */
    @Transactional
    public CandidateResponseDto createCandidateProfileByHr(
            final CandidateRequestDto request) {

        validateNoDuplicates(request.getEmail(), request.getMobileNumber());
        JobDescription jd = findActiveJdOrThrow(request.getJobDescriptionId());

        User user = new User(
                request.getFullName(),
                request.getEmail(),
                passwordEncoder.encode(request.getMobileNumber()),
                UserRole.CANDIDATE
        );
        user.setActive(true);
        User savedUser = userRepository.save(user);

        Candidate candidate = candidateMapper.mapToEntity(request, jd, savedUser);
        Candidate saved = candidateRepository.save(candidate);
        return candidateMapper.mapToResponseDto(saved);
    }

    /**
     * Returns all candidates for the HR dashboard.
     * HR needs to see every candidate regardless of their current stage.
     *
     * @return list of all candidates as response DTOs
     */
    public List<CandidateResponseDto> getAllCandidates() {
        return candidateRepository.findAll()
                .stream()
                .map(candidateMapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Returns a single candidate by ID for HR to view full details.
     *
     * @param id the candidate's database ID
     * @return the candidate as a response DTO
     * @throws jakarta.persistence.EntityNotFoundException if not found
     */
    public CandidateResponseDto getCandidateById(final Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Candidate not found with id: " + id
                ));
        return candidateMapper.mapToResponseDto(candidate);
    }

    /**
     * Returns the profile of the currently logged-in candidate.
     * Candidate can only see their own profile, not others.
     *
     * @param email the email of the logged-in candidate from JWT
     * @return the candidate's own profile as a response DTO
     * @throws jakarta.persistence.EntityNotFoundException if profile not found
     */
    public CandidateResponseDto getCandidateProfile(final String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "User not found with email: " + email
                ));

        Candidate candidate = candidateRepository.findByUser(user)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Candidate profile not found for this user"
                ));

        return candidateMapper.mapToResponseDto(candidate);
    }

    /**
     * Updates the resume path after a successful file upload.
     * Called internally by the controller after saving the file.
     *
     * @param candidateId the ID of the candidate
     * @param resumePath  the path where the resume was saved
     * @throws jakarta.persistence.EntityNotFoundException if not found
     */
    @Transactional
    public void updateResumePath(final Long candidateId,
                                  final String resumePath) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Candidate not found with id: " + candidateId
                ));
        candidate.setResumePath(resumePath);
        candidateRepository.save(candidate);
    }

    /**
     * Updates the current stage of a candidate (HR only).
     *
     * @param id    the candidate ID
     * @param stage the new interview stage
     * @return the updated candidate as a response DTO
     */
    @Transactional
    public CandidateResponseDto updateCandidateStage(final Long id, 
            final com.nucleusteq.interviewtracker.enums.InterviewStage stage) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Candidate not found with id: " + id
                ));
        candidate.setCurrentStage(stage);
        Candidate saved = candidateRepository.save(candidate);
        return candidateMapper.mapToResponseDto(saved);
    }

    /**
     * Checks for duplicate email or mobile before creating a profile.
     * Throws early so we don't waste time creating a user account first.
     *
     * @param email        the email to check
     * @param mobileNumber the mobile number to check
     * @throws IllegalArgumentException if either already exists
     */
    private void validateNoDuplicates(final String email,
                                       final String mobileNumber) {
        if (candidateRepository.existsByEmail(email)
                || userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists"
            );
        }
        if (candidateRepository.existsByMobileNumber(mobileNumber)) {
            throw new IllegalArgumentException(
                    "An account with this mobile number already exists"
            );
        }
    }

    /**
     * Fetches an active JD by ID or throws if not found or inactive.
     * Candidates should only apply to active job postings.
     *
     * @param jdId the job description ID
     * @return the active JobDescription entity
     * @throws jakarta.persistence.EntityNotFoundException if not found
     * @throws IllegalArgumentException if JD is inactive
     */
    private JobDescription findActiveJdOrThrow(final Long jdId) {
        JobDescription jd = jobDescriptionRepository.findById(jdId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Job description not found with id: " + jdId
                ));
        if (!jd.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot apply to an inactive job description"
            );
        }
        return jd;
    }
}