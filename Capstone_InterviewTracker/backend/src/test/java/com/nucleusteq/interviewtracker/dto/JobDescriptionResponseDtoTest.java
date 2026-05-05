package com.nucleusteq.interviewtracker.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JobDescriptionResponseDtoTest {

    @Test
    void allArgsConstructorAndGetters() {
        JobDescriptionResponseDto dto = new JobDescriptionResponseDto(
                1L,
                "Backend Engineer",
                "Write Java services",
                2,
                6,
                5.0,
                12.0,
                "Bengaluru",
                null,
                true,
                null,
                null
        );

        assertEquals(1L, dto.getId());
        assertEquals("Backend Engineer", dto.getJobTitle());
        assertEquals("Write Java services", dto.getJobDescription());
        assertEquals(Integer.valueOf(2), dto.getMinExperience());
    }

    @Test
    void defaultConstructorThenSetters() {
        JobDescriptionResponseDto dto = new JobDescriptionResponseDto();
        dto.setId(2L);
        dto.setJobTitle("Frontend Engineer");
        dto.setJobDescription("Build UI");
        dto.setLocation("Remote");

        assertEquals(2L, dto.getId());
        assertEquals("Frontend Engineer", dto.getJobTitle());
        assertEquals("Build UI", dto.getJobDescription());
        assertEquals("Remote", dto.getLocation());
    }

    @Test
    void defaultValuesAreNull() {
        JobDescriptionResponseDto dto = new JobDescriptionResponseDto();
        assertNull(dto.getJobTitle());
        assertNull(dto.getJobDescription());
    }
}
