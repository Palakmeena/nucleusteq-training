package com.nucleusteq.interviewtracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nucleusteq.interviewtracker.dto.CandidateRequestDto;
import com.nucleusteq.interviewtracker.dto.CandidateResponseDto;
import com.nucleusteq.interviewtracker.entity.CandidateProfile;
import com.nucleusteq.interviewtracker.service.CandidateProfileService;
import com.nucleusteq.interviewtracker.service.CandidateService;
import com.nucleusteq.interviewtracker.service.GoogleDriveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for CandidateController
 */
@ExtendWith(MockitoExtension.class)
class CandidateControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CandidateService candidateService;

    @Mock
    private GoogleDriveService googleDriveService;

    @Mock
    private CandidateProfileService profileService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CandidateController candidateController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(candidateController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private CandidateRequestDto createValidRequest() {
        CandidateRequestDto request = new CandidateRequestDto();
        request.setFullName("Test User");
        request.setEmail("test@example.com");
        request.setMobileCode("+91");
        request.setMobileNumber("9876543210");
        request.setCurrentOrganization("TechCorp");
        request.setTotalExperience(3.0);
        request.setRelevantExperience(2.0);
        request.setCurrentCtc(6.0);
        request.setExpectedCtc(10.0);
        request.setNoticePeriod(30);
        request.setPreferredLocation("Bangalore");
        request.setSource("LinkedIn");
        request.setGender("Male");
        request.setJobDescriptionId(1L);
        return request;
    }

    // ───── REGISTER CANDIDATE ─────

    @Test
    void registerCandidate_shouldReturn201_whenValid() throws Exception {

        CandidateRequestDto request = createValidRequest();

        CandidateResponseDto response = new CandidateResponseDto();
        response.setFullName("Test User");

        when(authentication.getName()).thenReturn("test@example.com");
        when(candidateService.createCandidateProfile(any(), any())).thenReturn(response);

        mockMvc.perform(post("/candidate/register")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.fullName").value("Test User"));
    }

    @Test
    void registerCandidate_shouldReturn401_whenNoAuth() throws Exception {
        // When no authentication is provided, the controller returns 401
        // Must provide valid JSON body to pass @RequestBody validation
        String validJson = "{\"fullName\":\"Test\",\"email\":\"test@test.com\",\"mobileCode\":\"+91\"," +
                "\"mobileNumber\":\"9876543210\",\"currentOrganization\":\"TechCorp\"," +
                "\"totalExperience\":3.0,\"relevantExperience\":2.0,\"currentCtc\":6.0," +
                "\"expectedCtc\":10.0,\"noticePeriod\":30,\"preferredLocation\":\"Bangalore\"," +
                "\"source\":\"LinkedIn\",\"gender\":\"Male\",\"jobDescriptionId\":1}";

        mockMvc.perform(post("/candidate/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerCandidate_shouldReturn400_whenInvalid() throws Exception {

        CandidateRequestDto request = createValidRequest();

        when(authentication.getName()).thenReturn("test@example.com");
        when(candidateService.createCandidateProfile(any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post("/candidate/register")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerCandidate_shouldReturn500_whenError() throws Exception {

        CandidateRequestDto request = createValidRequest();

        when(authentication.getName()).thenReturn("test@example.com");
        when(candidateService.createCandidateProfile(any(), any()))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/candidate/register")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // ───── GET ALL CANDIDATES (HR) ─────

    @Test
    void getAllCandidates_shouldReturn200() throws Exception {

        CandidateResponseDto dto = new CandidateResponseDto();
        dto.setFullName("Test User");

        when(candidateService.getAllCandidates()).thenReturn(List.of(dto));

        mockMvc.perform(get("/hr/candidates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].fullName").value("Test User"));
    }

    @Test
    void getAllCandidates_shouldReturn500_whenError() throws Exception {

        when(candidateService.getAllCandidates()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/hr/candidates"))
                .andExpect(status().isInternalServerError());
    }

    // ───── GET CANDIDATE BY ID ─────

    @Test
    void getCandidateById_shouldReturn200_whenFound() throws Exception {

        CandidateResponseDto dto = new CandidateResponseDto();
        dto.setFullName("Test User");

        when(candidateService.getCandidateById(1L)).thenReturn(dto);

        mockMvc.perform(get("/hr/candidate/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName").value("Test User"));
    }

    @Test
    void getCandidateById_shouldReturn404_whenNotFound() throws Exception {

        when(candidateService.getCandidateById(1L))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Not found"));

        mockMvc.perform(get("/hr/candidate/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCandidateById_shouldReturn500_whenError() throws Exception {

        when(candidateService.getCandidateById(1L))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/hr/candidate/1"))
                .andExpect(status().isInternalServerError());
    }

    // ───── GET OWN PROFILE ─────

    @Test
    void getCandidateProfile_shouldReturn200() throws Exception {

        CandidateProfile profile = new CandidateProfile();

        when(authentication.getName()).thenReturn("test@example.com");
        when(profileService.getProfileByEmail("test@example.com")).thenReturn(profile);

        mockMvc.perform(get("/candidate/profile")
                        .principal(authentication))
                .andExpect(status().isOk());
    }

    @Test
    void getCandidateProfile_shouldReturn500_whenError() throws Exception {

        when(authentication.getName()).thenReturn("test@example.com");
        when(profileService.getProfileByEmail("test@example.com"))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/candidate/profile")
                        .principal(authentication))
                .andExpect(status().isInternalServerError());
    }

    // ───── UPDATE PROFILE ─────

    @Test
    void updateCandidateProfile_shouldReturn200() throws Exception {
        CandidateProfile profile = new CandidateProfile();
        // Set minimal properties to avoid serialization issues
        // The actual profile will be returned by the mocked service

        when(authentication.getName()).thenReturn("test@example.com");
        when(profileService.updateProfile(any(), any())).thenReturn(profile);

        // Use a simple JSON object for the request body
        String jsonBody = "{\"fullName\":\"Test User\",\"mobileCode\":\"+91\",\"mobileNumber\":\"9876543210\"}";

        mockMvc.perform(put("/candidate/profile")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk());
    }

    @Test
    void updateCandidateProfile_shouldReturn500_whenError() throws Exception {

        when(authentication.getName()).thenReturn("test@example.com");
        when(profileService.updateProfile(any(), any()))
                .thenThrow(new RuntimeException("Database error"));

        String jsonBody = "{\"fullName\":\"Test User\",\"mobileCode\":\"+91\",\"mobileNumber\":\"9876543210\"}";

        mockMvc.perform(put("/candidate/profile")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isInternalServerError());
    }

    // ───── DELETE CANDIDATE ─────

    @Test
    void deleteCandidate_shouldReturn200() throws Exception {

        mockMvc.perform(delete("/hr/candidate/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCandidate_shouldReturn404_whenNotFound() throws Exception {

        doThrow(new jakarta.persistence.EntityNotFoundException("Not found"))
                .when(candidateService).deleteCandidate(1L);

        mockMvc.perform(delete("/hr/candidate/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCandidate_shouldReturn500_whenError() throws Exception {

        doThrow(new RuntimeException("Database error"))
                .when(candidateService).deleteCandidate(1L);

        mockMvc.perform(delete("/hr/candidate/1"))
                .andExpect(status().isInternalServerError());
    }
}