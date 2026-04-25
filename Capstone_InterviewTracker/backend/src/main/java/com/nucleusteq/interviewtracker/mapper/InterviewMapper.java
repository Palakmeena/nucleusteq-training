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
        InterviewResponseDto dto = new InterviewResponseDto();
        dto.setId(interview.getId());
        dto.setCandidateId(interview.getCandidate().getId());
        dto.setCandidateName(interview.getCandidate().getFullName());
        dto.setInterviewStage(interview.getInterviewStage());
        dto.setInterviewDate(interview.getInterviewDate());
        dto.setInterviewTime(interview.getInterviewTime());
        dto.setFocusAreas(interview.getFocusAreas());
        dto.setHrComments(interview.getHrComments());
        dto.setCompleted(interview.isCompleted());
        dto.setCreatedAt(interview.getCreatedAt());

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

        dto.setPanelMemberNames(panelNames);
        dto.setPanelMemberIds(panelIds);
        return dto;
    }
}