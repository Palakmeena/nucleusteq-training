package com.nucleusteq.interviewtracker.repository;

import com.nucleusteq.interviewtracker.entity.JobDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for JobDescription entity.
 */
@Repository
public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {

    /**
     * Fetches all job descriptions that are currently active.
     */
    List<JobDescription> findByIsActive(boolean isActive);

    /**
     * Fetches all job descriptions ordered by creation date descending.
     * Used by HR dashboard to see all JDs — both active and inactive —
     * with newest ones at the top.
     */
    List<JobDescription> findAllByOrderByCreatedAtDesc();
}