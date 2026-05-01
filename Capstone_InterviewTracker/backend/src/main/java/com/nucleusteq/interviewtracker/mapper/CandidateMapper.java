package com.nucleusteq.interviewtracker.mapper;

import com.nucleusteq.interviewtracker.dto.CandidateRequestDto;
import com.nucleusteq.interviewtracker.dto.CandidateResponseDto;
import com.nucleusteq.interviewtracker.entity.Candidate;
import com.nucleusteq.interviewtracker.entity.JobDescription;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.enums.InterviewStage;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Candidate entity and DTOs.
 * Keeps mapping logic out of the service layer.
 */
@Component
public class CandidateMapper {

    /**
     * Builds a Candidate entity from request DTO, JD, and user.
     * Sets stage to PROFILING explicitly for clarity.
     *
     * @param request   the profiling form data
     * @param jd        the job description entity
     * @param savedUser the linked user account
     * @return a new Candidate entity ready to be saved
     */
    public Candidate mapToEntity(final CandidateRequestDto request,
                                  final JobDescription jd,
                                  final User savedUser) {
        Candidate candidate = new Candidate(
                request.getFullName(),
                request.getEmail(),
                request.getMobileCode(),
                request.getMobileNumber(),
                request.getCurrentOrganization(),
                request.getTotalExperience(),
                request.getRelevantExperience(),
                request.getCurrentCtc(),
                request.getExpectedCtc(),
                request.getNoticePeriod(),
                request.getPreferredLocation(),
                request.getSource(),
                jd,
                savedUser
        );

        if (request.getDateOfBirth() != null) {
            candidate.setDateOfBirth(request.getDateOfBirth());
        }

        candidate.setGender(request.getGender());
        candidate.setCurrentStage(InterviewStage.PROFILING);
        return candidate;
    }

    /**
     * Converts a Candidate entity to a response DTO.
     * Extracts JD id and title so frontend doesn't need a separate call.
     *
     * @param candidate the entity from the database
     * @return the mapped response DTO
     */
    public CandidateResponseDto mapToResponseDto(final Candidate candidate) {
        CandidateResponseDto candidateResponseDto = new CandidateResponseDto();
        candidateResponseDto.setId(candidate.getId());
        candidateResponseDto.setFullName(candidate.getFullName());
        candidateResponseDto.setEmail(candidate.getEmail());
        candidateResponseDto.setMobileCode(candidate.getMobileCode());
        candidateResponseDto.setMobileNumber(candidate.getMobileNumber());
        candidateResponseDto.setDateOfBirth(candidate.getDateOfBirth());
        candidateResponseDto.setResumePath(candidate.getResumePath());
        candidateResponseDto.setCurrentOrganization(candidate.getCurrentOrganization());
        candidateResponseDto.setTotalExperience(candidate.getTotalExperience());
        candidateResponseDto.setRelevantExperience(candidate.getRelevantExperience());
        candidateResponseDto.setCurrentCtc(candidate.getCurrentCtc());
        candidateResponseDto.setExpectedCtc(candidate.getExpectedCtc());
        candidateResponseDto.setNoticePeriod(candidate.getNoticePeriod());
        candidateResponseDto.setPreferredLocation(candidate.getPreferredLocation());
        candidateResponseDto.setSource(candidate.getSource());
        candidateResponseDto.setGender(candidate.getGender());
        candidateResponseDto.setCurrentStage(candidate.getCurrentStage());
        candidateResponseDto.setCreatedAt(candidate.getCreatedAt());
        candidateResponseDto.setJobDescriptionId(candidate.getJobDescription().getId());
        candidateResponseDto.setJobTitle(candidate.getJobDescription().getJobTitle());
        return candidateResponseDto;
    }
}