package com.nucleusteq.interviewtracker.mapper;

import com.nucleusteq.interviewtracker.dto.InterviewResponseDto;
import com.nucleusteq.interviewtracker.entity.Interview;
import com.nucleusteq.interviewtracker.entity.InterviewPanel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Interview entity and DTOs.
 * Extracts panel member names and IDs from InterviewPanel assignments.
 */
@Component
public class InterviewMapper {

    /**
     * Converts an Interview entity to a response DTO.
     * Extracts panel member names and IDs from the assigned panels list.
     *
     * @param interview the entity from the database
     * @return the mapped response DTO
     */
    public InterviewResponseDto mapToResponseDto(final Interview interview) {
        InterviewResponseDto interviewResponseDto = new InterviewResponseDto();
        interviewResponseDto.setId(interview.getId());
        interviewResponseDto.setCandidateId(interview.getCandidate().getId());
        interviewResponseDto.setCandidateName(interview.getCandidate().getFullName());
        interviewResponseDto.setInterviewStage(interview.getInterviewStage());
        interviewResponseDto.setInterviewDate(interview.getInterviewDate());
        interviewResponseDto.setInterviewTime(interview.getInterviewTime());
        interviewResponseDto.setFocusAreas(interview.getFocusAreas());
        interviewResponseDto.setHrComments(interview.getHrComments());
        interviewResponseDto.setCompleted(interview.isCompleted());
        interviewResponseDto.setCreatedAt(interview.getCreatedAt());

        /*
         * Extract panel member names and IDs from the InterviewPanel
         * join table. Frontend needs names for display and IDs for
         * any further HR management operations.
         */
        List<String> panelNames = interview.getInterviewPanels()
                .stream()
                .map(ip -> ip.getPanelMember().getFullName())
                .collect(Collectors.toList());

        List<Long> panelIds = interview.getInterviewPanels()
                .stream()
                .map(ip -> ip.getPanelMember().getId())
                .collect(Collectors.toList());

        interviewResponseDto.setPanelMemberNames(panelNames);
        interviewResponseDto.setPanelMemberIds(panelIds);

        // Map new fields for Panel/Candidate dashboards
        interviewResponseDto.setMeetingLink(interview.getMeetingLink());
        interviewResponseDto.setResumeUrl(interview.getCandidate().getResumePath());
        
        if (interview.getCandidate().getJobDescription() != null) {
            interviewResponseDto.setJdId(interview.getCandidate().getJobDescription().getId());
            interviewResponseDto.setJdTitle(interview.getCandidate().getJobDescription().getJobTitle());
            interviewResponseDto.setJdDetails(interview.getCandidate().getJobDescription().getJobDescription());
        }

        /*
         * Map panel feedbacks so HR can view evaluations.
         */
        if (interview.getFeedbacks() != null) {
            interviewResponseDto.setFeedbacks(interview.getFeedbacks().stream().map(f -> {
                com.nucleusteq.interviewtracker.dto.FeedbackResponseDto fr = new com.nucleusteq.interviewtracker.dto.FeedbackResponseDto();
                fr.setPanelMemberName(f.getPanelMember() != null ? f.getPanelMember().getFullName() : "HR Admin");
                fr.setRating(f.getRating());
                fr.setComments(f.getComments());
                fr.setStrengths(f.getStrengths());
                fr.setWeaknesses(f.getWeaknesses());
                fr.setDecision(f.getFeedbackStatus().name());
                fr.setPanelSuggestion(f.getPanelSuggestion()); // preserve original suggestion
                fr.setSubmittedAt(f.getSubmittedAt());
                return fr;
            }).collect(Collectors.toList()));
        }

        return interviewResponseDto;
    }
}