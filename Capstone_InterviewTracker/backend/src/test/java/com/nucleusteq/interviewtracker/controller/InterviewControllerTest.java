package com.nucleusteq.interviewtracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nucleusteq.interviewtracker.dto.FeedbackRequestDto;
import com.nucleusteq.interviewtracker.dto.InterviewRequestDto;
import com.nucleusteq.interviewtracker.dto.InterviewResponseDto;
import com.nucleusteq.interviewtracker.enums.InterviewStage;
import com.nucleusteq.interviewtracker.service.InterviewService;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for InterviewController
 */
@ExtendWith(MockitoExtension.class)
class InterviewControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InterviewService interviewService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private InterviewController interviewController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(interviewController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private InterviewRequestDto createValidInterviewRequest() {
        InterviewRequestDto request = new InterviewRequestDto();
        request.setCandidateId(1L);
        request.setInterviewStage(InterviewStage.L1_TECHNICAL);
        request.setInterviewDate(LocalDate.now().plusDays(1));
        request.setInterviewTime(LocalTime.of(10, 0));
        request.setFocusAreas("Java, Spring Boot");
        request.setPanelMemberIds(List.of(1L));
        return request;
    }

    private FeedbackRequestDto createValidFeedbackRequest() {
        FeedbackRequestDto request = new FeedbackRequestDto();
        request.setRating(4);
        request.setComments("Good technical skills");
        request.setDecision("RECOMMENDED");
        return request;
    }

    // ───── SCHEDULE INTERVIEW ─────

    @Test
    void scheduleInterview_shouldReturn201_whenValid() throws Exception {
        InterviewRequestDto request = createValidInterviewRequest();

        InterviewResponseDto response = new InterviewResponseDto();
        response.setId(1L);

        when(interviewService.scheduleInterview(any())).thenReturn(response);

        mockMvc.perform(post("/hr/interview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void scheduleInterview_shouldReturn400_whenInvalid() throws Exception {
        // Create request with valid data, service will throw exception
        InterviewRequestDto request = createValidInterviewRequest();

        when(interviewService.scheduleInterview(any()))
                .thenThrow(new IllegalArgumentException("Invalid date"));

        mockMvc.perform(post("/hr/interview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void scheduleInterview_shouldReturn404_whenEntityNotFound() throws Exception {
        InterviewRequestDto request = createValidInterviewRequest();

        when(interviewService.scheduleInterview(any()))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Not found"));

        mockMvc.perform(post("/hr/interview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ───── GET BY CANDIDATE ID ─────

    @Test
    void getInterviewsByCandidate_shouldReturn200() throws Exception {

        InterviewResponseDto dto = new InterviewResponseDto();
        dto.setId(1L);

        when(interviewService.getInterviewsByCandidate(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/hr/interview/candidate/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    void getInterviewsByCandidate_shouldReturn404_whenNotFound() throws Exception {

        when(interviewService.getInterviewsByCandidate(1L))
                .thenThrow(new jakarta.persistence.EntityNotFoundException());

        mockMvc.perform(get("/hr/interview/candidate/1"))
                .andExpect(status().isNotFound());
    }

    // ───── GET BY ID ─────

    @Test
    void getInterviewById_shouldReturn200() throws Exception {

        InterviewResponseDto dto = new InterviewResponseDto();
        dto.setId(1L);

        when(interviewService.getInterviewById(1L)).thenReturn(dto);

        mockMvc.perform(get("/hr/interview/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    // ───── PANEL VIEW ─────

    @Test
    void getInterviewsForPanel_shouldReturn200() throws Exception {

        when(authentication.getName()).thenReturn("panel@example.com");
        when(interviewService.getInterviewsForPanel("panel@example.com"))
                .thenReturn(List.of(new InterviewResponseDto()));

        mockMvc.perform(get("/panel/interviews")
                        .principal(authentication))
                .andExpect(status().isOk());
    }

    // ───── CANDIDATE VIEW ─────

    @Test
    void getInterviewsForCandidate_shouldReturn200() throws Exception {

        when(authentication.getName()).thenReturn("candidate@example.com");
        when(interviewService.getInterviewsForCandidate("candidate@example.com"))
                .thenReturn(List.of(new InterviewResponseDto()));

        mockMvc.perform(get("/candidate/interviews")
                        .principal(authentication))
                .andExpect(status().isOk());
    }

    // ───── PANEL FEEDBACK ─────

    @Test
    void submitFeedback_shouldReturn200() throws Exception {
        FeedbackRequestDto request = createValidFeedbackRequest();

        when(authentication.getName()).thenReturn("panel@example.com");

        mockMvc.perform(put("/panel/interview/1/feedback")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void submitFeedback_shouldReturn404_whenNotFound() throws Exception {
        FeedbackRequestDto request = createValidFeedbackRequest();

        when(authentication.getName()).thenReturn("panel@example.com");

        doThrow(new jakarta.persistence.EntityNotFoundException())
                .when(interviewService)
                .submitFeedback(any(), any(), any());

        mockMvc.perform(put("/panel/interview/1/feedback")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ───── HR FEEDBACK ─────

    @Test
    void submitHrFeedback_shouldReturn200() throws Exception {
        FeedbackRequestDto request = createValidFeedbackRequest();

        mockMvc.perform(put("/hr/interview/1/hr-feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void submitHrFeedback_shouldReturn400_whenInvalid() throws Exception {
        FeedbackRequestDto request = createValidFeedbackRequest();

        doThrow(new IllegalArgumentException("Invalid"))
                .when(interviewService)
                .submitHrFeedback(any(), any());

        mockMvc.perform(put("/hr/interview/1/hr-feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}