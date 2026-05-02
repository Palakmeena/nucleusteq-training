package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.entity.CandidateProfile;
import com.nucleusteq.interviewtracker.entity.User;
import com.nucleusteq.interviewtracker.enums.UserRole;
import com.nucleusteq.interviewtracker.repository.CandidateProfileRepository;
import com.nucleusteq.interviewtracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateProfileServiceTest {

    @Mock
    private CandidateProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CandidateProfileService candidateProfileService;

    private User testUser;
    private CandidateProfile existingProfile;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "test@example.com", "password", UserRole.CANDIDATE);
        testUser.setId(1L);

        existingProfile = new CandidateProfile(testUser);
        existingProfile.setId(1L);
        existingProfile.setFullName("Original Name");
        existingProfile.setMobileCode("+91");
        existingProfile.setMobileNumber("9876543210");
        existingProfile.setEmail("test@example.com");
    }

    @Test
    void getProfileByEmail_shouldReturnExistingProfile() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(profileRepository.findByUser(testUser)).thenReturn(Optional.of(existingProfile));

        CandidateProfile result = candidateProfileService.getProfileByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("Original Name", result.getFullName());
        assertEquals("test@example.com", result.getEmail());
        verify(profileRepository, never()).save(any());
    }

    @Test
    void getProfileByEmail_shouldCreateNewProfile_whenNotExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(profileRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(profileRepository.save(any(CandidateProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CandidateProfile result = candidateProfileService.getProfileByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(profileRepository).save(any(CandidateProfile.class));
    }

    @Test
    void getProfileByEmail_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> candidateProfileService.getProfileByEmail("unknown@example.com"));
    }

    @Test
    void updateProfile_shouldUpdateAllFields() {
        CandidateProfile updatedInfo = new CandidateProfile();
        updatedInfo.setFullName("Updated Name");
        updatedInfo.setMobileCode("+1");
        updatedInfo.setMobileNumber("5551234567");
        updatedInfo.setDateOfBirth(LocalDate.of(1990, 5, 15));
        updatedInfo.setCurrentOrganization("New Company");
        updatedInfo.setTotalExperience(5.0);
        updatedInfo.setRelevantExperience(3.0);
        updatedInfo.setCurrentCtc(10.0);
        updatedInfo.setExpectedCtc(15.0);
        updatedInfo.setNoticePeriod(30);
        updatedInfo.setPreferredLocation("Bangalore");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(profileRepository.findByUser(testUser)).thenReturn(Optional.of(existingProfile));
        when(profileRepository.save(any(CandidateProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CandidateProfile result = candidateProfileService.updateProfile("test@example.com", updatedInfo);

        assertEquals("Updated Name", result.getFullName());
        assertEquals("+1", result.getMobileCode());
        assertEquals("5551234567", result.getMobileNumber());
        assertEquals(LocalDate.of(1990, 5, 15), result.getDateOfBirth());
        assertEquals("New Company", result.getCurrentOrganization());
        assertEquals(5.0, result.getTotalExperience());
        assertEquals(3.0, result.getRelevantExperience());
        assertEquals(10.0, result.getCurrentCtc());
        assertEquals(15.0, result.getExpectedCtc());
        assertEquals(30, result.getNoticePeriod());
        assertEquals("Bangalore", result.getPreferredLocation());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void updateProfile_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> candidateProfileService.updateProfile("unknown@example.com", new CandidateProfile()));
    }

    @Test
    void updateResume_shouldUpdateResumePath() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(profileRepository.findByUser(testUser)).thenReturn(Optional.of(existingProfile));
        when(profileRepository.save(any(CandidateProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        candidateProfileService.updateResume("test@example.com", "/uploads/resumes/test_resume.pdf");

        assertEquals("/uploads/resumes/test_resume.pdf", existingProfile.getResumePath());
        assertNotNull(existingProfile.getUpdatedAt());
        verify(profileRepository).save(existingProfile);
    }

    @Test
    void updateResume_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> candidateProfileService.updateResume("unknown@example.com", "/path/to/resume.pdf"));
    }

    @Test
    void updateResume_shouldCreateNewProfile_whenNotExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(profileRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(profileRepository.save(any(CandidateProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        candidateProfileService.updateResume("test@example.com", "/uploads/resumes/test_resume.pdf");

        verify(profileRepository, times(2)).save(any(CandidateProfile.class)); // Once for create, once for update
    }
}
