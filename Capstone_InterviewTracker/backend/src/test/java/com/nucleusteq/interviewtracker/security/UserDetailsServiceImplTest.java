package com.nucleusteq.interviewtracker.security;

import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.enums.UserRole;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "test@example.com", "encodedPassword", UserRole.HR);
        testUser.setActive(true);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR")));
    }

    @Test
    void loadUserByUsername_shouldReturnDisabledUserDetails_whenUserInactive() {
        testUser.setActive(false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent@example.com"));
    }

    @Test
    void loadUserByUsername_shouldReturnCorrectAuthorities_forHR() {
        testUser.setRole(UserRole.HR);
        when(userRepository.findByEmail("hr@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("hr@example.com");

        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR")));
    }

    @Test
    void loadUserByUsername_shouldReturnCorrectAuthorities_forCandidate() {
        testUser.setRole(UserRole.CANDIDATE);
        when(userRepository.findByEmail("candidate@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("candidate@example.com");

        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CANDIDATE")));
    }

    @Test
    void loadUserByUsername_shouldReturnCorrectAuthorities_forPanel() {
        testUser.setRole(UserRole.PANEL);
        when(userRepository.findByEmail("panel@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("panel@example.com");

        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PANEL")));
    }
}
