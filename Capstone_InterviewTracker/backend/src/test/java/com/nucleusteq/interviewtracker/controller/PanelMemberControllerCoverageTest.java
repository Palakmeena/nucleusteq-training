package com.nucleusteq.interviewtracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nucleusteq.interviewtracker.dto.PanelMemberResponseDto;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.enums.UserRole;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import com.nucleusteq.interviewtracker.service.CandidateService;
import com.nucleusteq.interviewtracker.service.PanelMemberService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PanelMemberControllerCoverageTest {

    @Mock
    private PanelMemberService panelMemberService;

    @Mock
    private CandidateService candidateService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PanelMemberController panelMemberController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(panelMemberController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllPanelMembers_shouldReturnOk() throws Exception {
        PanelMemberResponseDto responseDto = new PanelMemberResponseDto();
        responseDto.setEmail("panel@example.com");
        when(panelMemberService.getAllPanelMembers()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/hr/panels").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].email").value("panel@example.com"));
    }

    @Test
    void getPanelMemberById_shouldReturnOk() throws Exception {
        PanelMemberResponseDto responseDto = new PanelMemberResponseDto();
        responseDto.setEmail("single@example.com");
        when(panelMemberService.getPanelMemberById(5L)).thenReturn(responseDto);

        mockMvc.perform(get("/hr/panel/5").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("single@example.com"));
    }

    @Test
    void activateAccount_shouldRouteToPanelMemberService_whenPanelUser() throws Exception {
        User user = new User("Panel One", "panel@example.com", "old", UserRole.PANEL);
        when(userRepository.findByActivationToken("token-1")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/auth/activate")
                        .param("token", "token-1")
                        .param("password", "cGFzc3dvcmQ="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(panelMemberService).activatePanelMember("token-1", "password");
    }

    @Test
    void activateAccount_shouldRouteToCandidateService_whenCandidateUser() throws Exception {
        User user = new User("Cand One", "cand@example.com", "old", UserRole.CANDIDATE);
        when(userRepository.findByActivationToken("token-2")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/auth/activate")
                        .param("token", "token-2")
                        .param("password", "cGFzc3dvcmQ="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(candidateService).activateCandidateAccount("token-2", "password");
    }

    @Test
    void getPanelMemberProfile_shouldReturnOk_whenAuthenticated() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("panel@example.com");
        PanelMemberResponseDto responseDto = new PanelMemberResponseDto();
        responseDto.setEmail("panel@example.com");
        when(panelMemberService.getPanelMemberProfile("panel@example.com")).thenReturn(responseDto);

        mockMvc.perform(get("/panel/profile").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("panel@example.com"));
    }
}
