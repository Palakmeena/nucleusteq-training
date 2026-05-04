package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.dto.LoginRequestDto;
import com.nucleusteq.interviewtracker.dto.LoginResponseDto;
import com.nucleusteq.interviewtracker.dto.SignupRequestDto;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.enums.UserRole;
import com.nucleusteq.interviewtracker.mapper.AuthMapper;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import com.nucleusteq.interviewtracker.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User hrUser;
    private LoginRequestDto loginRequest;

    @BeforeEach
    void setUp() {

        hrUser = new User("HR Admin", "hr@example.com", "encodedPass", UserRole.HR);
        hrUser.setActive(true);

        loginRequest = new LoginRequestDto();
        loginRequest.setEmail("hr@example.com");
        loginRequest.setPassword("password123");
    }

    // ---------------- LOGIN ----------------

    @Test
    void login_shouldReturnToken_whenSuccess() {

        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        when(auth.getName()).thenReturn("hr@example.com");

        when(userRepository.findByEmail("hr@example.com"))
                .thenReturn(Optional.of(hrUser));

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("mock.jwt.token");

        LoginResponseDto response =
                new LoginResponseDto("mock.jwt.token", "HR", "HR Admin", "hr@example.com");

        when(authMapper.mapToLoginResponse(any(User.class), anyString()))
                .thenReturn(response);

        LoginResponseDto result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals("mock.jwt.token", result.getToken());
    }

    @Test
    void login_shouldThrowDisabledException() {

        when(authenticationManager.authenticate(any()))
                .thenThrow(new DisabledException("disabled"));

        assertThrows(DisabledException.class,
                () -> authService.login(loginRequest));
    }

    @Test
    void login_shouldThrowBadCredentialsException() {

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad creds"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest));
    }

    // ---------------- SIGNUP ----------------

    @Test
    void signup_shouldCreateUser_successfully() {

        SignupRequestDto request = new SignupRequestDto();
        request.setFullName("Sarah Gupta");
        request.setEmail("sarah@example.com");
        request.setMobileCode("+91");
        request.setMobileNumber("9876543210");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setGender("Female");

        when(userRepository.findByEmail("sarah@example.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(authMapper.mapToLoginResponse(any(User.class), any()))
                .thenReturn(new LoginResponseDto("signup.token", "CANDIDATE",
                        "Sarah Gupta", "sarah@example.com"));

        LoginResponseDto result = authService.signup(request, passwordEncoder);

        assertNotNull(result);
        assertEquals("signup.token", result.getToken());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void signup_shouldThrow_whenEmailAlreadyExists() {

        SignupRequestDto request = new SignupRequestDto();
        request.setFullName("Duplicate User");
        request.setEmail("existing@example.com");
        request.setMobileCode("+91");
        request.setMobileNumber("9999999999");
        request.setDateOfBirth(LocalDate.of(2001, 5, 10));
        request.setGender("Male");

        User existing = new User();
        existing.setActive(true);

        when(userRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(existing));

        assertThrows(com.nucleusteq.interviewtracker.exception.BusinessException.class,
                () -> authService.signup(request, passwordEncoder));

        verify(userRepository, never()).save(any());
    }

    // ────────────── VERIFY CANDIDATE ──────────────

    @Test
    void verifyCandidate_shouldVerify_whenTokenValid() {

        User candidate = new User("Candidate Name", "candidate@example.com", "pass", UserRole.CANDIDATE);
        candidate.setActivationToken("valid-token");
        candidate.setActive(false);
                candidate.setTokenExpiry(java.time.LocalDateTime.now().plusHours(1));

        when(userRepository.findByActivationToken("valid-token"))
                .thenReturn(Optional.of(candidate));

        authService.verifyCandidate("valid-token");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void verifyCandidate_shouldThrow_whenTokenInvalid() {

        when(userRepository.findByActivationToken("invalid-token"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> authService.verifyCandidate("invalid-token"));
    }

    @Test
    void verifyCandidate_shouldThrow_whenTokenExpired() {

        User candidate = new User("Candidate", "candidate@test.com", "pass", UserRole.CANDIDATE);
        candidate.setActivationToken("expired-token");
        candidate.setActive(false);
        candidate.setTokenExpiry(java.time.LocalDateTime.now().minusHours(1));

        when(userRepository.findByActivationToken("expired-token"))
                .thenReturn(Optional.of(candidate));

        assertThrows(com.nucleusteq.interviewtracker.exception.BusinessException.class,
                () -> authService.verifyCandidate("expired-token"));
    }

    // ────────────── EXTRA TESTS FOR LOGIN/SIGNUP ──────────────

    @Test
    void login_shouldHandleMultipleLogins() {

        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        when(auth.getName()).thenReturn("hr@example.com");

        when(userRepository.findByEmail("hr@example.com"))
                .thenReturn(Optional.of(hrUser));

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("first.jwt.token");

        LoginResponseDto response1 =
                new LoginResponseDto("first.jwt.token", "HR", "HR Admin", "hr@example.com");

        when(authMapper.mapToLoginResponse(any(User.class), anyString()))
                .thenReturn(response1);

        LoginResponseDto result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals("first.jwt.token", result.getToken());

        // Second login with different token
        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("second.jwt.token");

        LoginResponseDto response2 =
                new LoginResponseDto("second.jwt.token", "HR", "HR Admin", "hr@example.com");

        when(authMapper.mapToLoginResponse(any(User.class), anyString()))
                .thenReturn(response2);

        LoginResponseDto result2 = authService.login(loginRequest);

        assertNotNull(result2);
        assertEquals("second.jwt.token", result2.getToken());
    }

    @Test
    void signup_shouldCreateCandidateProfile() {

        SignupRequestDto request = new SignupRequestDto();
        request.setFullName("Sarah Gupta");
        request.setEmail("sarah@example.com");
        request.setMobileCode("+91");
        request.setMobileNumber("9876543210");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setGender("Female");

        when(userRepository.findByEmail("sarah@example.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(authMapper.mapToLoginResponse(any(User.class), any()))
                .thenReturn(new LoginResponseDto("signup.token", "CANDIDATE",
                        "Sarah Gupta", "sarah@example.com"));

        LoginResponseDto result = authService.signup(request, passwordEncoder);

        assertNotNull(result);
        assertEquals("CANDIDATE", result.getRole());
        assertEquals("sarah@example.com", result.getEmail());
    }

}