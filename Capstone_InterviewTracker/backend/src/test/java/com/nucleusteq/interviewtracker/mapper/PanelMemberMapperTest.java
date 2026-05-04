package com.nucleusteq.interviewtracker.mapper;

import com.nucleusteq.interviewtracker.dto.PanelMemberRequestDto;
import com.nucleusteq.interviewtracker.dto.PanelMemberResponseDto;
import com.nucleusteq.interviewtracker.entity.PanelMember;
import com.nucleusteq.interviewtracker.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for PanelMemberMapper.
 * Verifies entity creation and response DTO mapping.
 */
class PanelMemberMapperTest {

    private PanelMemberMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PanelMemberMapper();
    }

    private PanelMemberRequestDto buildRequest() {
        PanelMemberRequestDto req = new PanelMemberRequestDto();
        req.setFullName("John Rao");
        req.setEmail("john@techcorp.com");
        req.setMobileNumber("9876543210");
        req.setOrganization("TechCorp");
        req.setDesignation("Senior Developer");
        return req;
    }

    @Test
    void mapToEntity_shouldMapAllFields() {
        PanelMemberRequestDto request = buildRequest();

        PanelMember result = mapper.mapToEntity(request);

        assertNotNull(result);
        // fullName and email come from User entity (not mapped in mapToEntity)
        assertNull(result.getFullName());
        assertNull(result.getEmail());
        assertEquals("9876543210", result.getMobileNumber());
        assertEquals("TechCorp", result.getOrganization());
        assertEquals("Senior Developer", result.getDesignation());
    }

    @Test
    void mapToEntity_shouldStartAsInactive() {
        PanelMember result = mapper.mapToEntity(buildRequest());

        assertFalse(result.isActive());
    }

    @Test
    void mapToResponseDto_shouldMapAllFields() {
        PanelMember panelMember = new PanelMember(
                "9876543210", "TechCorp", "Senior Developer"
        );
        User user = new User("John Rao", "john@techcorp.com", "pass", com.nucleusteq.interviewtracker.enums.UserRole.PANEL);
        panelMember.setUser(user);

        PanelMemberResponseDto result = mapper.mapToResponseDto(panelMember);

        assertNotNull(result);
        assertEquals("John Rao", result.getFullName());
        assertEquals("john@techcorp.com", result.getEmail());
        assertEquals("9876543210", result.getMobileNumber());
        assertEquals("TechCorp", result.getOrganization());
        assertEquals("Senior Developer", result.getDesignation());
        assertFalse(result.isActive());
    }
}
