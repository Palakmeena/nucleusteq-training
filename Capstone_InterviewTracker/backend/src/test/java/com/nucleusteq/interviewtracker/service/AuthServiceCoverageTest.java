package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.dto.LoginResponseDto;
import com.nucleusteq.interviewtracker.dto.SignupRequestDto;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.enums.UserRole;
import com.nucleusteq.interviewtracker.exception.BusinessException;
import com.nucleusteq.interviewtracker.mapper.AuthMapper;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import com.nucleusteq.interviewtracker.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceCoverageTest {

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

    @Test
    void signup_shouldResendActivationWhenUserExistsAndInactive() {
        SignupRequestDto request = new SignupRequestDto();
        request.setFullName("Maya Singh");
        request.setEmail("maya@example.com");

        User user = new User("Maya Singh", "maya@example.com", "temp", UserRole.CANDIDATE);
        user.setActive(false);

        LoginResponseDto responseDto = new LoginResponseDto();

        when(userRepository.findByEmail("maya@example.com")).thenReturn(Optional.of(user));
        when(authMapper.mapToLoginResponse(user, null)).thenReturn(responseDto);

        LoginResponseDto result = authService.signup(request, passwordEncoder);

        assertEquals(responseDto, result);
        verify(userRepository).save(user);
    }

    @Test
    void signup_shouldThrowWhenUserExistsAndActive() {
        SignupRequestDto request = new SignupRequestDto();
        request.setFullName("Maya Singh");
        request.setEmail("maya@example.com");

        User user = new User("Maya Singh", "maya@example.com", "temp", UserRole.CANDIDATE);
        user.setActive(true);

        when(userRepository.findByEmail("maya@example.com")).thenReturn(Optional.of(user));

        assertThrows(BusinessException.class, () -> authService.signup(request, passwordEncoder));
    }

    @Test
    void login_shouldThrowWhenUserMissingAfterAuthentication() {
        com.nucleusteq.interviewtracker.dto.LoginRequestDto request = new com.nucleusteq.interviewtracker.dto.LoginRequestDto();
        request.setEmail("missing@example.com");
        request.setPassword("encoded");

        Authentication authentication = new UsernamePasswordAuthenticationToken("missing@example.com", "encoded");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }
}
