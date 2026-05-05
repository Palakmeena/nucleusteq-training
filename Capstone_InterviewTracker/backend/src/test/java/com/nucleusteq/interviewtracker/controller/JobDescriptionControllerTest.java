package com.nucleusteq.interviewtracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nucleusteq.interviewtracker.dto.JobDescriptionRequestDto;
import com.nucleusteq.interviewtracker.dto.JobDescriptionResponseDto;
import com.nucleusteq.interviewtracker.enums.JobType;
import com.nucleusteq.interviewtracker.service.JobDescriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for JobDescriptionController
 */
@ExtendWith(MockitoExtension.class)
class JobDescriptionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JobDescriptionService jobDescriptionService;

    @InjectMocks
    private JobDescriptionController controller;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    private JobDescriptionRequestDto createValidRequest() {
        JobDescriptionRequestDto request = new JobDescriptionRequestDto();
        request.setJobTitle("Backend Developer");
        request.setJobDescription("Develop and maintain backend services using Java and Spring Boot");
        request.setMinExperience(2);
        request.setMaxExperience(5);
        request.setMinSalary(8.0);
        request.setMaxSalary(15.0);
        request.setLocation("Bangalore");
        request.setJobType(JobType.FULL_TIME);
        request.setSkills(List.of("Java", "Spring Boot", "MySQL"));
        return request;
    }

    // ───── CREATE JD ─────

    @Test
    void createJobDescription_shouldReturn201_whenValid() throws Exception {
        JobDescriptionRequestDto request = createValidRequest();

        JobDescriptionResponseDto response = new JobDescriptionResponseDto();
        response.setJobTitle("Backend Developer");

        when(jobDescriptionService.createJobDescription(any())).thenReturn(response);

        mockMvc.perform(post("/hr/jd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.jobTitle").value("Backend Developer"));
    }

    @Test
    void createJobDescription_shouldReturn400_whenInvalid() throws Exception {
        JobDescriptionRequestDto request = createValidRequest();

        when(jobDescriptionService.createJobDescription(any()))
                .thenThrow(new IllegalArgumentException("Job already exists"));

        mockMvc.perform(post("/hr/jd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createJobDescription_shouldReturn500_whenUnexpected() throws Exception {
        JobDescriptionRequestDto request = createValidRequest();

        when(jobDescriptionService.createJobDescription(any()))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/hr/jd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // ───── GET ALL ACTIVE ─────

    @Test
    void getAllActiveJds_shouldReturn200() throws Exception {

        JobDescriptionResponseDto dto = new JobDescriptionResponseDto();
        dto.setJobTitle("Backend Dev");

        when(jobDescriptionService.getAllActiveJobDescriptions())
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/jd/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].jobTitle").value("Backend Dev"));
    }

    @Test
    void getAllJdsForHr_shouldReturn200() throws Exception {

        JobDescriptionResponseDto dto = new JobDescriptionResponseDto();
        dto.setJobTitle("HR View JD");

        when(jobDescriptionService.getAllJobDescriptionsForHr())
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/hr/jd/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].jobTitle").value("HR View JD"));
    }

    @Test
    void getAllActiveJds_shouldReturn500_whenError() throws Exception {

        when(jobDescriptionService.getAllActiveJobDescriptions())
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/jd/all"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllJdsForHr_shouldReturn500_whenError() throws Exception {

        when(jobDescriptionService.getAllJobDescriptionsForHr())
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/hr/jd/all"))
                .andExpect(status().isInternalServerError());
    }

    // ───── GET BY ID ─────

    @Test
    void getJdById_shouldReturn200() throws Exception {

        JobDescriptionResponseDto dto = new JobDescriptionResponseDto();
        dto.setJobTitle("Backend Dev");

        when(jobDescriptionService.getJobDescriptionById(1L)).thenReturn(dto);

        mockMvc.perform(get("/jd/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.jobTitle").value("Backend Dev"));
    }

    @Test
    void getJdById_shouldReturn404_whenNotFound() throws Exception {

        when(jobDescriptionService.getJobDescriptionById(1L))
                .thenThrow(new jakarta.persistence.EntityNotFoundException());

        mockMvc.perform(get("/jd/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getJdById_shouldReturn500_whenError() throws Exception {

        when(jobDescriptionService.getJobDescriptionById(1L))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/jd/1"))
                .andExpect(status().isInternalServerError());
    }

    // ───── UPDATE ─────

    @Test
    void updateJobDescription_shouldReturn200() throws Exception {
        JobDescriptionRequestDto request = createValidRequest();
        request.setJobTitle("Senior Backend Developer");

        JobDescriptionResponseDto response = new JobDescriptionResponseDto();
        response.setJobTitle("Senior Backend Developer");

        when(jobDescriptionService.updateJobDescription(any(), any())).thenReturn(response);

        mockMvc.perform(put("/hr/jd/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.jobTitle").value("Senior Backend Developer"));
    }

    @Test
    void updateJobDescription_shouldReturn404_whenNotFound() throws Exception {
        JobDescriptionRequestDto request = createValidRequest();

        doThrow(new jakarta.persistence.EntityNotFoundException("Not found"))
                .when(jobDescriptionService).updateJobDescription(any(), any());

        mockMvc.perform(put("/hr/jd/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateJobDescription_shouldReturn400_whenInvalid() throws Exception {
        JobDescriptionRequestDto request = createValidRequest();

        doThrow(new IllegalArgumentException("Invalid data"))
                .when(jobDescriptionService).updateJobDescription(any(), any());

        mockMvc.perform(put("/hr/jd/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateJobDescription_shouldReturn500_whenError() throws Exception {
        JobDescriptionRequestDto request = createValidRequest();

        doThrow(new RuntimeException("Database error"))
                .when(jobDescriptionService).updateJobDescription(any(), any());

        mockMvc.perform(put("/hr/jd/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // ───── DELETE ─────

    @Test
    void deleteJobDescription_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/hr/jd/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteJobDescription_shouldReturn404() throws Exception {

        doThrow(new jakarta.persistence.EntityNotFoundException())
                .when(jobDescriptionService).deleteJobDescription(1L);

        mockMvc.perform(delete("/hr/jd/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteJobDescription_shouldReturn409() throws Exception {

        doThrow(new DataIntegrityViolationException("Constraint"))
                .when(jobDescriptionService).deleteJobDescription(1L);

        mockMvc.perform(delete("/hr/jd/1"))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteJobDescription_shouldReturn500_whenError() throws Exception {

        doThrow(new RuntimeException("Database error"))
                .when(jobDescriptionService).deleteJobDescription(1L);

        mockMvc.perform(delete("/hr/jd/1"))
                .andExpect(status().isInternalServerError());
    }
}