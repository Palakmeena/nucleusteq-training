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
        PanelMemberResponseDto panelMemberResponseDto = new PanelMemberResponseDto();
        panelMemberResponseDto.setId(panelMember.getId());
        panelMemberResponseDto.setFullName(panelMember.getFullName());
        panelMemberResponseDto.setEmail(panelMember.getEmail());
        panelMemberResponseDto.setMobileNumber(panelMember.getMobileNumber());
        panelMemberResponseDto.setOrganization(panelMember.getOrganization());
        panelMemberResponseDto.setDesignation(panelMember.getDesignation());
        panelMemberResponseDto.setActive(panelMember.isActive());
        panelMemberResponseDto.setCreatedAt(panelMember.getCreatedAt());
        return panelMemberResponseDto;
    }
}