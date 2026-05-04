package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.dto.PanelMemberRequestDto;
import com.nucleusteq.interviewtracker.dto.PanelMemberResponseDto;
import com.nucleusteq.interviewtracker.entity.PanelMember;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.enums.UserRole;
import com.nucleusteq.interviewtracker.mapper.PanelMemberMapper;
import com.nucleusteq.interviewtracker.repository.PanelMemberRepository;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import com.nucleusteq.interviewtracker.repository.FeedbackRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PanelMemberServiceTest {

    @Mock private PanelMemberRepository panelMemberRepository;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private PanelMemberMapper panelMemberMapper;
    @Mock private JavaMailSender mailSender;
    @Mock private FeedbackRepository feedbackRepository;
    @Mock private InterviewService interviewService;

    @InjectMocks private PanelMemberService service;

    private PanelMemberRequestDto request;
    private PanelMember panelMember;
    private PanelMemberResponseDto response;
    private User user;

    @BeforeEach
    void setUp() {
        request = new PanelMemberRequestDto();
        request.setFullName("John Rao");
        request.setEmail("john@techcorp.com");
        request.setMobileNumber("9876543210");

        panelMember = new PanelMember(
                "John Rao", "john@techcorp.com",
                "9876543210", "TechCorp", "Senior Dev"
        );

        response = new PanelMemberResponseDto();
        response.setFullName("John Rao");

        user = new User("John Rao", "john@techcorp.com", "pass", UserRole.PANEL);
    }

    // ✅ CREATE

    @Test
    void create_shouldWork() {
        when(panelMemberRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(panelMemberRepository.existsByMobileNumber(any())).thenReturn(false);

        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(user);
        when(panelMemberMapper.mapToEntity(request)).thenReturn(panelMember);
        when(panelMemberRepository.save(any())).thenReturn(panelMember);
        when(panelMemberMapper.mapToResponseDto(panelMember)).thenReturn(response);

        PanelMemberResponseDto result = service.createPanelMember(request);

        assertNotNull(result);
        verify(userRepository).save(any());
        verify(panelMemberRepository).save(any());
    }

    // ❗ DUPLICATE EMAIL

    @Test
    void create_shouldThrow_whenEmailExists() {
        when(panelMemberRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> service.createPanelMember(request));
    }

    // ❗ DUPLICATE MOBILE

    @Test
    void create_shouldThrow_whenMobileExists() {
        when(panelMemberRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(panelMemberRepository.existsByMobileNumber(any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> service.createPanelMember(request));
    }

    // ✅ ACTIVATE

    @Test
    void activate_shouldWork() {
        user.setActivationToken("token");
        user.setTokenExpiry(LocalDateTime.now().plusHours(1));

        when(userRepository.findByActivationToken("token"))
                .thenReturn(Optional.of(user));
        when(panelMemberRepository.findByUser(user))
                .thenReturn(Optional.of(panelMember));
        when(passwordEncoder.encode("pass"))
                .thenReturn("encoded");

        service.activatePanelMember("token", "pass");

        assertTrue(user.isActive());
        assertNull(user.getActivationToken());
        assertNull(user.getTokenExpiry());

        verify(userRepository).save(user);
        verify(panelMemberRepository).save(panelMember);
    }

    @Test
    void activate_shouldThrowInvalidToken() {
        when(userRepository.findByActivationToken("bad"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.activatePanelMember("bad", "pass"));
    }

    @Test
    void activate_shouldThrowExpired() {
        user.setTokenExpiry(LocalDateTime.now().minusHours(1));

        when(userRepository.findByActivationToken("token"))
                .thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> service.activatePanelMember("token", "pass"));
    }

    // ✅ GET BY ID

    @Test
    void getById_shouldReturn() {
        when(panelMemberRepository.findById(1L))
                .thenReturn(Optional.of(panelMember));
        when(panelMemberMapper.mapToResponseDto(panelMember))
                .thenReturn(response);

        assertNotNull(service.getPanelMemberById(1L));
    }

    @Test
    void getById_shouldThrow() {
        when(panelMemberRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.getPanelMemberById(99L));
    }

    // ────────────── GET ALL ──────────────

    @Test
    void getAll_shouldReturnList() {
        when(panelMemberRepository.findAll())
                .thenReturn(java.util.List.of(panelMember, panelMember));
        when(panelMemberMapper.mapToResponseDto(any()))
                .thenReturn(response);

        var result = service.getAllPanelMembers();

        assertEquals(2, result.size());
        verify(panelMemberRepository).findAll();
    }

    // ────────────── GET PROFILE ──────────────

    @Test
    void getProfile_shouldReturn_whenFound() {
        when(userRepository.findByEmail("john@techcorp.com"))
                .thenReturn(Optional.of(user));
        when(panelMemberRepository.findByUser(user))
                .thenReturn(Optional.of(panelMember));
        when(panelMemberMapper.mapToResponseDto(panelMember))
                .thenReturn(response);

        var result = service.getPanelMemberProfile("john@techcorp.com");

        assertNotNull(result);
    }

    @Test
    void getProfile_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail("notfound@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.getPanelMemberProfile("notfound@test.com"));
    }

    // ────────────── UPDATE ──────────────

    @Test
    void update_shouldWork() {
        panelMember.setUser(user);
        
        when(panelMemberRepository.findById(1L))
                .thenReturn(Optional.of(panelMember));
        when(panelMemberRepository.save(any()))
                .thenReturn(panelMember);
        when(panelMemberMapper.mapToResponseDto(panelMember))
                .thenReturn(response);

        var result = service.updatePanelMember(1L, request);

        assertNotNull(result);
        verify(panelMemberRepository).save(any());
    }

    @Test
    void update_shouldThrow_whenNotFound() {
        when(panelMemberRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.updatePanelMember(99L, request));
    }

    // ────────────── DELETE ──────────────

    @Test
    void delete_shouldWork() {
        when(panelMemberRepository.findById(1L))
                .thenReturn(Optional.of(panelMember));

        service.deletePanelMember(1L);

        verify(panelMemberRepository).delete(panelMember);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(panelMemberRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.deletePanelMember(99L));
    }
}