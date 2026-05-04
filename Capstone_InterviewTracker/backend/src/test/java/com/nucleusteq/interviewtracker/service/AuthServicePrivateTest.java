package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.mapper.AuthMapper;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import com.nucleusteq.interviewtracker.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthServicePrivateTest {

    @Mock
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthMapper authMapper;
    @Mock
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    @Test
    void sendActivationEmail_shouldTrimFrontendUrlAndCallMailSender() throws Exception {
        AuthService svc = new AuthService(authenticationManager, userRepository, jwtUtil, authMapper, mailSender);

        // set frontendBaseUrl to value with trailing slash
        Field f = AuthService.class.getDeclaredField("frontendBaseUrl");
        f.setAccessible(true);
        f.set(svc, "http://example.com/");

        // set fromEmail to non-blank so setFrom branch is hit
        Field fe = AuthService.class.getDeclaredField("fromEmail");
        fe.setAccessible(true);
        fe.set(svc, "noreply@example.com");

        Method m = AuthService.class.getDeclaredMethod("sendActivationEmail", String.class, String.class, String.class);
        m.setAccessible(true);

        // invoke and verify mailSender called without exception
        m.invoke(svc, "to@example.com", "Name", "token-123");

        verify(mailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }
}
