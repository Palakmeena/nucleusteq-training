package com.nucleusteq.interviewtracker.service;

import com.nucleusteq.interviewtracker.dto.JobDescriptionRequestDto;
import com.nucleusteq.interviewtracker.dto.JobDescriptionResponseDto;
import com.nucleusteq.interviewtracker.entity.JobDescription;
import com.nucleusteq.interviewtracker.enums.JobType;
import com.nucleusteq.interviewtracker.mapper.JobDescriptionMapper;
import com.nucleusteq.interviewtracker.repository.JobDescriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobDescriptionServiceTest {

    @Mock
    private JobDescriptionRepository jobDescriptionRepository;

    @Mock
    private JobDescriptionMapper jobDescriptionMapper;

    @InjectMocks
    private JobDescriptionService jobDescriptionService;

    private JobDescriptionRequestDto request;
    private JobDescription jd;
    private JobDescriptionResponseDto response;

    @BeforeEach
    void setUp() {
        request = new JobDescriptionRequestDto();
        request.setJobTitle("Backend Developer");
        request.setJobDescription("Build APIs");
        request.setMinExperience(1);
        request.setMaxExperience(3);
        request.setMinSalary(4.0);
        request.setMaxSalary(8.0);
        request.setLocation("Bangalore");
        request.setJobType(JobType.FULL_TIME);

        jd = new JobDescription(
                "Backend Developer", "Build APIs",
                1, 3, 4.0, 8.0,
                "Bangalore", JobType.FULL_TIME
        );

        response = new JobDescriptionResponseDto();
        response.setJobTitle("Backend Developer");
    }

    @Test
    void createJobDescription_shouldWork() {
        when(jobDescriptionMapper.mapToEntity(request)).thenReturn(jd);
        when(jobDescriptionRepository.save(jd)).thenReturn(jd);
        when(jobDescriptionMapper.mapToResponseDto(jd)).thenReturn(response);

        JobDescriptionResponseDto result =
                jobDescriptionService.createJobDescription(request);

        assertNotNull(result);
        assertEquals("Backend Developer", result.getJobTitle());
    }

    @Test
    void createJobDescription_shouldThrowInvalidExp() {
        request.setMinExperience(5);
        request.setMaxExperience(2);

        assertThrows(IllegalArgumentException.class,
                () -> jobDescriptionService.createJobDescription(request));
    }

    @Test
    void getAllActive_shouldReturnList() {
        when(jobDescriptionRepository.findByIsActive(true))
                .thenReturn(List.of(jd));
        when(jobDescriptionMapper.mapToResponseDto(jd))
                .thenReturn(response);

        List<JobDescriptionResponseDto> result =
                jobDescriptionService.getAllActiveJobDescriptions();

        assertEquals(1, result.size());
    }

    @Test
    void getById_shouldReturn() {
        when(jobDescriptionRepository.findById(1L))
                .thenReturn(Optional.of(jd));
        when(jobDescriptionMapper.mapToResponseDto(jd))
                .thenReturn(response);

        JobDescriptionResponseDto result =
                jobDescriptionService.getJobDescriptionById(1L);

        assertNotNull(result);
    }

    @Test
    void getById_shouldThrow() {
        when(jobDescriptionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> jobDescriptionService.getJobDescriptionById(99L));
    }

    @Test
    void update_shouldWork() {
        when(jobDescriptionRepository.findById(1L))
                .thenReturn(Optional.of(jd));
        when(jobDescriptionRepository.save(jd))
                .thenReturn(jd);
        when(jobDescriptionMapper.mapToResponseDto(jd))
                .thenReturn(response);

        JobDescriptionResponseDto result =
                jobDescriptionService.updateJobDescription(1L, request);

        assertNotNull(result);
        verify(jobDescriptionMapper).updateEntityFromRequest(jd, request);
    }

    // ────────────── DELETE JD ──────────────

    @Test
    void deleteJobDescription_shouldDelete_whenExists() {
        when(jobDescriptionRepository.findById(1L))
                .thenReturn(Optional.of(jd));

        jobDescriptionService.deleteJobDescription(1L);

        verify(jobDescriptionRepository).delete(jd);
    }

    @Test
    void deleteJobDescription_shouldThrow_whenNotFound() {
        when(jobDescriptionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> jobDescriptionService.deleteJobDescription(99L));
    }

    // ────────────── GET ALL FOR HR ──────────────

    @Test
    void getAllJobDescriptionsForHr_shouldReturnAllIncludingInactive() {
        JobDescription activeJd = new JobDescription(
                "Backend Dev", "Build APIs", 1, 3, 4.0, 8.0, "Bangalore", JobType.FULL_TIME
        );
        activeJd.setActive(true);

        JobDescription inactiveJd = new JobDescription(
                "Frontend Dev", "Build UIs", 2, 4, 3.0, 7.0, "Mumbai", JobType.FULL_TIME
        );
        inactiveJd.setActive(false);

        when(jobDescriptionRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(activeJd, inactiveJd));
        when(jobDescriptionMapper.mapToResponseDto(any()))
                .thenReturn(response);

        List<JobDescriptionResponseDto> result =
                jobDescriptionService.getAllJobDescriptionsForHr();

                assertEquals(2, result.size());
                verify(jobDescriptionRepository).findAllByOrderByCreatedAtDesc();
    }
}