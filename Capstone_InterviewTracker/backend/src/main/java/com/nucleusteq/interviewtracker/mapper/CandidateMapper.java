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
        CandidateResponseDto dto = new CandidateResponseDto();
        dto.setId(candidate.getId());
        dto.setFullName(candidate.getFullName());
        dto.setEmail(candidate.getEmail());
        dto.setMobileCode(candidate.getMobileCode());
        dto.setMobileNumber(candidate.getMobileNumber());
        dto.setDateOfBirth(candidate.getDateOfBirth());
        dto.setResumePath(candidate.getResumePath());
        dto.setCurrentOrganization(candidate.getCurrentOrganization());
        dto.setTotalExperience(candidate.getTotalExperience());
        dto.setRelevantExperience(candidate.getRelevantExperience());
        dto.setCurrentCtc(candidate.getCurrentCtc());
        dto.setExpectedCtc(candidate.getExpectedCtc());
        dto.setNoticePeriod(candidate.getNoticePeriod());
        dto.setPreferredLocation(candidate.getPreferredLocation());
        dto.setSource(candidate.getSource());
        dto.setCurrentStage(candidate.getCurrentStage());
        dto.setCreatedAt(candidate.getCreatedAt());
        dto.setJobDescriptionId(candidate.getJobDescription().getId());
        dto.setJobTitle(candidate.getJobDescription().getJobTitle());
        return dto;
    }
}