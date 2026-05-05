package com.nucleusteq.interviewtracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nucleusteq.interviewtracker.dto.LoginRequestDto;
import com.nucleusteq.interviewtracker.dto.LoginResponseDto;
import com.nucleusteq.interviewtracker.dto.SignupRequestDto;
import com.nucleusteq.interviewtracker.service.AuthService;
import com.nucleusteq.interviewtracker.util.AppConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthController
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;
    private final String BASE_URL = AppConstants.AUTH_BASE;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private LoginRequestDto createValidLoginRequest() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        return request;
    }

    private SignupRequestDto createValidSignupRequest() {
        SignupRequestDto request = new SignupRequestDto();
        request.setFullName("Test User");
        request.setEmail("test@example.com");
        request.setMobileCode("+91");
        request.setMobileNumber("9876543210");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        return request;
    }

    // ───────── LOGIN ─────────

    @Test
    void login_shouldReturn200() throws Exception {

        LoginResponseDto response =
                new LoginResponseDto("token", "HR", "Test", "test@mail.com");

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidLoginRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("token"));
    }

    @Test
    void login_shouldReturn403() throws Exception {

        when(authService.login(any()))
                .thenThrow(new DisabledException("Disabled"));

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidLoginRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    void login_shouldReturn401() throws Exception {

        when(authService.login(any()))
                .thenThrow(new BadCredentialsException("Bad"));

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidLoginRequest())))
                .andExpect(status().isUnauthorized());
    }

    // ───────── SIGNUP ─────────

    @Test
    void signup_shouldReturn201() throws Exception {

        LoginResponseDto response =
                new LoginResponseDto("token", "CANDIDATE", "Test", "test@mail.com");

        when(authService.signup(any(), any())).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidSignupRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.role").value("CANDIDATE"));
    }

    @Test
    void signup_shouldReturn400() throws Exception {

        when(authService.signup(any(), any()))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        mockMvc.perform(post(BASE_URL + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidSignupRequest())))
                .andExpect(status().isBadRequest());
    }

    // ───────── EXTRA ERROR PATH TESTS ─────────

    @Test
    void login_shouldReturn500_whenUnexpectedError() throws Exception {

        when(authService.login(any()))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidLoginRequest())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(AppConstants.SOMETHING_WENT_WRONG));
    }

    @Test
    void signup_shouldReturn500_whenUnexpectedError() throws Exception {

        when(authService.signup(any(), any()))
                .thenThrow(new RuntimeException("Email service error"));

        mockMvc.perform(post(BASE_URL + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidSignupRequest())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(AppConstants.SOMETHING_WENT_WRONG));
    }
}