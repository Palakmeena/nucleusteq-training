package com.nucleusteq.interviewtracker.mapper;

import com.nucleusteq.interviewtracker.dto.JobDescriptionRequestDto;
import com.nucleusteq.interviewtracker.dto.JobDescriptionResponseDto;
import com.nucleusteq.interviewtracker.entity.JobDescription;
import com.nucleusteq.interviewtracker.entity.Skill;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for JobDescription entities and DTOs.
 */
@Component
public class JobDescriptionMapper {

    /**
     * Maps JobDescriptionRequestDto to JobDescription entity.
     *
     * @param request the job description request
     * @return the mapped JobDescription entity
     */
    public JobDescription mapToEntity(JobDescriptionRequestDto request) {
        JobDescription jd = new JobDescription(
                request.getJobTitle(),
                request.getJobDescription(),
                request.getMinExperience(),
                request.getMaxExperience(),
                request.getMinSalary(),
                request.getMaxSalary(),
                request.getLocation(),
                request.getJobType()
        );

        List<Skill> skillEntities = request.getSkills().stream()
                .map(skillName -> new Skill(skillName.trim(), jd))
                .collect(Collectors.toList());
        jd.setSkills(skillEntities);

        return jd;
    }

    /**
     * Maps JobDescription entity to JobDescriptionResponseDto.
     *
     * @param jd the job description entity
     * @return the mapped response DTO
     */
    public JobDescriptionResponseDto mapToResponseDto(JobDescription jd) {
        List<String> skillNames = jd.getSkills().stream()
                .map(Skill::getSkillName)
                .collect(Collectors.toList());

        return new JobDescriptionResponseDto(
                jd.getId(),
                jd.getJobTitle(),
                jd.getJobDescription(),
                jd.getMinExperience(),
                jd.getMaxExperience(),
                jd.getMinSalary(),
                jd.getMaxSalary(),
                jd.getLocation(),
                jd.getJobType(),
                jd.isActive(),
                jd.getCreatedAt(),
                skillNames
        );
    }

    /**
     * Updates an existing JobDescription entity from a request DTO.
     *
     * @param jd      the existing job description entity
     * @param request the request DTO with updated fields
     */
    public void updateEntityFromRequest(JobDescription jd, JobDescriptionRequestDto request) {
        jd.setJobTitle(request.getJobTitle());
        jd.setJobDescription(request.getJobDescription());
        jd.setMinExperience(request.getMinExperience());
        jd.setMaxExperience(request.getMaxExperience());
        jd.setMinSalary(request.getMinSalary());
        jd.setMaxSalary(request.getMaxSalary());
        jd.setLocation(request.getLocation());
        jd.setJobType(request.getJobType());

        jd.getSkills().clear();
        List<Skill> updatedSkills = request.getSkills().stream()
                .map(skillName -> new Skill(skillName.trim(), jd))
                .collect(Collectors.toList());
        jd.getSkills().addAll(updatedSkills);
    }
}
