package com.nucleusteq.interviewtracker.service;

/**
 * Service class for job description operations.
 * Manages job creation and retrieval.
 */

import com.nucleusteq.interviewtracker.dto.JobDescriptionRequestDto;
import com.nucleusteq.interviewtracker.dto.JobDescriptionResponseDto;
import com.nucleusteq.interviewtracker.entity.JobDescription;
import com.nucleusteq.interviewtracker.mapper.JobDescriptionMapper;
import com.nucleusteq.interviewtracker.repository.JobDescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class that handles business logic for Job Description management.
 */
@Service
public class JobDescriptionService {

    private final JobDescriptionRepository jobDescriptionRepository;
    private final JobDescriptionMapper jobDescriptionMapper;

    /**
     * Constructor injection for repository and mapper.
     */
    @Autowired
    public JobDescriptionService(JobDescriptionRepository jobDescriptionRepository,
                                 JobDescriptionMapper jobDescriptionMapper) {
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.jobDescriptionMapper = jobDescriptionMapper;
    }

    /**
     * Creates a new Job Description.
     *
     * @param request the JD request DTO
     * @return the saved JD response DTO
     */
    @Transactional
    public JobDescriptionResponseDto createJobDescription(JobDescriptionRequestDto request) {
        validateExperienceRange(request.getMinExperience(), request.getMaxExperience());
        validateSalaryRange(request.getMinSalary(), request.getMaxSalary());

        JobDescription jd = jobDescriptionMapper.mapToEntity(request);
        JobDescription saved = jobDescriptionRepository.save(jd);
        return jobDescriptionMapper.mapToResponseDto(saved);
    }

    /**
     * Returns all active job descriptions.
     *
     * @return list of active JDs
     */
    public List<JobDescriptionResponseDto> getAllActiveJobDescriptions() {
        return jobDescriptionRepository.findByIsActive(true).stream()
                .map(jobDescriptionMapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Returns all job descriptions for HR (including inactive), sorted by newest first.
     *
     * @return list of all JDs
     */
    public List<JobDescriptionResponseDto> getAllJobDescriptionsForHr() {
        return jobDescriptionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(jobDescriptionMapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Returns a single job description by ID.
     *
     * @param id the JD ID
     * @return the JD response DTO
     */
    public JobDescriptionResponseDto getJobDescriptionById(Long id) {
        JobDescription jd = findJdByIdOrThrow(id);
        return jobDescriptionMapper.mapToResponseDto(jd);
    }

    /**
     * Updates an existing job description.
     *
     * @param id      the JD ID
     * @param request the updated JD data
     * @return the updated JD response DTO
     */
    @Transactional
    public JobDescriptionResponseDto updateJobDescription(Long id, JobDescriptionRequestDto request) {
        validateExperienceRange(request.getMinExperience(), request.getMaxExperience());
        validateSalaryRange(request.getMinSalary(), request.getMaxSalary());

        JobDescription jd = findJdByIdOrThrow(id);
        jobDescriptionMapper.updateEntityFromRequest(jd, request);

        JobDescription updated = jobDescriptionRepository.save(jd);
        return jobDescriptionMapper.mapToResponseDto(updated);
    }

    /**
     * Hard deletes a job description from the database.
     *
     * @param id the JD ID
     */
    @Transactional
    public void deleteJobDescription(Long id) {
        JobDescription jd = findJdByIdOrThrow(id);
        jobDescriptionRepository.delete(jd);
    }

    /**
     * Fetches a JD by ID or throws an exception if not found.
     */
    private JobDescription findJdByIdOrThrow(Long id) {
        return jobDescriptionRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Job description not found with id: " + id
                ));
    }

    /**
     * Validates that minExperience is not greater than maxExperience.
     */
    private void validateExperienceRange(Integer min, Integer max) {
        if (min > max) {
            throw new IllegalArgumentException(
                    "Minimum experience cannot be greater than maximum experience"
            );
        }
    }

    /**
     * Validates that minSalary is not greater than maxSalary.
     */
    private void validateSalaryRange(Double min, Double max) {
        if (min > max) {
            throw new IllegalArgumentException(
                    "Minimum salary cannot be greater than maximum salary"
            );
        }
    }
}