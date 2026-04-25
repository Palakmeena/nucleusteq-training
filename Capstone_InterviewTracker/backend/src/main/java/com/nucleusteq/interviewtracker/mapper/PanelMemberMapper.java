package com.nucleusteq.interviewtracker.mapper;

import com.nucleusteq.interviewtracker.dto.PanelMemberRequestDto;
import com.nucleusteq.interviewtracker.dto.PanelMemberResponseDto;
import com.nucleusteq.interviewtracker.entity.PanelMember;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between PanelMember entity and DTOs.
 * Keeps mapping logic out of the service layer.
 */
@Component
public class PanelMemberMapper {

    /**
     * Builds a PanelMember entity from the request DTO.
     * isActive starts as false — activated later via email link.
     *
     * @param request the panel member creation form data
     * @return a new PanelMember entity ready to be saved
     */
    public PanelMember mapToEntity(final PanelMemberRequestDto request) {
        return new PanelMember(
                request.getFullName(),
                request.getEmail(),
                request.getMobileNumber(),
                request.getOrganization(),
                request.getDesignation()
        );
    }

    /**
     * Converts a PanelMember entity to a response DTO.
     * Never exposes activation token or password fields.
     *
     * @param panelMember the entity from the database
     * @return the mapped response DTO
     */
    public PanelMemberResponseDto mapToResponseDto(final PanelMember panelMember) {
        PanelMemberResponseDto dto = new PanelMemberResponseDto();
        dto.setId(panelMember.getId());
        dto.setFullName(panelMember.getFullName());
        dto.setEmail(panelMember.getEmail());
        dto.setMobileNumber(panelMember.getMobileNumber());
        dto.setOrganization(panelMember.getOrganization());
        dto.setDesignation(panelMember.getDesignation());
        dto.setActive(panelMember.isActive());
        dto.setCreatedAt(panelMember.getCreatedAt());
        return dto;
    }
}