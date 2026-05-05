package com.nucleusteq.interviewtracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nucleusteq.interviewtracker.dto.PanelMemberRequestDto;
import com.nucleusteq.interviewtracker.dto.PanelMemberResponseDto;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import com.nucleusteq.interviewtracker.service.CandidateService;
import com.nucleusteq.interviewtracker.service.PanelMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.persistence.EntityNotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PanelMemberControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PanelMemberService panelMemberService;

    @Mock
    private CandidateService candidateService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PanelMemberController panelMemberController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(panelMemberController).build();
        objectMapper = new ObjectMapper();
    }

    private PanelMemberRequestDto createValidRequest() {
        PanelMemberRequestDto request = new PanelMemberRequestDto();
        request.setFullName("John Rao");
        request.setEmail("john@test.com");
        request.setDesignation("SDE");
        request.setOrganization("TechCorp");
        request.setMobileNumber("9876543210");
        return request;
    }

    // ---------------- CREATE SUCCESS ----------------

    @Test
    void createPanelMember_shouldReturn201() throws Exception {
        PanelMemberRequestDto request = createValidRequest();

        PanelMemberResponseDto response = new PanelMemberResponseDto();
        response.setFullName("John Rao");

        when(panelMemberService.createPanelMember(any()))
                .thenReturn(response);

        mockMvc.perform(post("/hr/panel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // ---------------- CREATE INVALID (400) ----------------

    @Test
    void createPanelMember_shouldReturn400_whenInvalid() throws Exception {
        PanelMemberRequestDto request = createValidRequest();

        when(panelMemberService.createPanelMember(any()))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        mockMvc.perform(post("/hr/panel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ────────────── CREATE 500 ERROR ──────────────

    @Test
    void createPanelMember_shouldReturn500_whenError() throws Exception {
        PanelMemberRequestDto request = createValidRequest();

        when(panelMemberService.createPanelMember(any()))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/hr/panel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // ---------------- UPDATE SUCCESS ----------------

    @Test
    void updatePanelMember_shouldReturn200() throws Exception {
        PanelMemberRequestDto request = createValidRequest();

        PanelMemberResponseDto response = new PanelMemberResponseDto();
        response.setFullName("John Rao");

        when(panelMemberService.updatePanelMember(Mockito.eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(put("/hr/panel/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ---------------- UPDATE NOT FOUND ----------------

    @Test
    void updatePanelMember_shouldReturn404() throws Exception {
        PanelMemberRequestDto request = createValidRequest();

        when(panelMemberService.updatePanelMember(Mockito.eq(99L), any()))
                .thenThrow(new EntityNotFoundException("Not found"));

        mockMvc.perform(put("/hr/panel/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ────────────── UPDATE 400 ERROR ──────────────

    @Test
    void updatePanelMember_shouldReturn500_whenValidationError() throws Exception {
        PanelMemberRequestDto request = createValidRequest();

        when(panelMemberService.updatePanelMember(Mockito.eq(1L), any()))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(put("/hr/panel/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // ────────────── UPDATE 500 ERROR ──────────────

    @Test
    void updatePanelMember_shouldReturn500_whenError() throws Exception {
        PanelMemberRequestDto request = createValidRequest();

        when(panelMemberService.updatePanelMember(Mockito.eq(1L), any()))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(put("/hr/panel/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // ---------------- DELETE ----------------

    @Test
    void deletePanelMember_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/hr/panel/1"))
                .andExpect(status().isOk());
    }

    // ────────────── DELETE 404 ERROR ──────────────

    @Test
    void deletePanelMember_shouldReturn404_whenNotFound() throws Exception {

        doThrow(new EntityNotFoundException("Not found"))
                .when(panelMemberService).deletePanelMember(1L);

        mockMvc.perform(delete("/hr/panel/1"))
                .andExpect(status().isNotFound());
    }

    // ────────────── DELETE 500 ERROR ──────────────

    @Test
    void deletePanelMember_shouldReturn500_whenError() throws Exception {

        doThrow(new RuntimeException("Database error"))
                .when(panelMemberService).deletePanelMember(1L);

        mockMvc.perform(delete("/hr/panel/1"))
                .andExpect(status().isInternalServerError());
    }
}