package com.nucleusteq.interviewtracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nucleusteq.interviewtracker.dto.CandidateRequestDto;
import com.nucleusteq.interviewtracker.dto.CandidateResponseDto;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CandidateControllerCoverageTest {

    @Mock
    private CandidateService candidateService;
    @Mock
    private GoogleDriveService googleDriveService;
    @Mock
    private CandidateProfileService profileService;

    @InjectMocks
    private CandidateController candidateController;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(candidateController).build();
    }

    @Test
    void registerCandidate_shouldReturnUnauthorized_whenNoAuth() throws Exception {
        CandidateRequestDto req = new CandidateRequestDto();
        req.setEmail("a@b.com");
        req.setFullName("X User");
        req.setMobileCode("+91");
        req.setMobileNumber("9999999999");
        req.setCurrentOrganization("Org");
        req.setTotalExperience(1.0);
        req.setRelevantExperience(1.0);
        req.setCurrentCtc(1.0);
        req.setExpectedCtc(1.0);
        req.setNoticePeriod(0);
        req.setPreferredLocation("Remote");
        req.setSource("LinkedIn");
        req.setGender("M");
        req.setJobDescriptionId(1L);

        mockMvc.perform(post("/candidate/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(candidateService);
    }

    @Test
    void createCandidateByHr_shouldReturnCreated_onSuccess() throws Exception {
        CandidateRequestDto req = new CandidateRequestDto();
        req.setEmail("hr.add@example.com");
        req.setFullName("HR Add");
        req.setMobileCode("+91");
        req.setMobileNumber("8888888888");
        req.setCurrentOrganization("Org");
        req.setTotalExperience(2.0);
        req.setRelevantExperience(2.0);
        req.setCurrentCtc(2.0);
        req.setExpectedCtc(2.5);
        req.setNoticePeriod(30);
        req.setPreferredLocation("Onsite");
        req.setSource("Referral");
        req.setGender("F");
        req.setJobDescriptionId(2L);

        CandidateResponseDto resp = new CandidateResponseDto();
        resp.setEmail("hr.add@example.com");
        resp.setFullName("HR Add");

        when(candidateService.createCandidateProfileByHr(any())).thenReturn(resp);

        mockMvc.perform(post("/hr/candidate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("hr.add@example.com"));

        verify(candidateService).createCandidateProfileByHr(any());
    }

    @Test
    void uploadProfileResume_shouldRejectInvalidFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "data".getBytes());

        mockMvc.perform(multipart("/candidate/profile/resume").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(googleDriveService);
    }

    @Test
    void uploadResumeByCandidateId_shouldRejectInvalidFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "data".getBytes());

        mockMvc.perform(multipart("/candidate/resume/1").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(candidateService);
    }

    @Test
    void updateStage_shouldReturnNotFound_whenServiceThrows() throws Exception {
        when(candidateService.updateCandidateStage(eq(999L), any())).thenThrow(new jakarta.persistence.EntityNotFoundException("not found"));

        mockMvc.perform(put("/hr/candidate/999/stage").param("stage", "SCREENING"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteCandidate_shouldReturnNotFound_whenServiceThrows() throws Exception {
        doThrow(new jakarta.persistence.EntityNotFoundException("not found")).when(candidateService).deleteCandidate(888L);

        mockMvc.perform(delete("/hr/candidate/888"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
