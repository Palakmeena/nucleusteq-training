package com.nucleusteq.interviewtracker.repository;

import com.nucleusteq.interviewtracker.entity.Candidate;
import com.nucleusteq.interviewtracker.entity.Interview;
import com.nucleusteq.interviewtracker.enums.InterviewStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Interview entity.
 * Provides database operations for interview scheduling.
 */
@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    /**
     * Finds all interviews for a specific candidate.
     * HR uses this to see the full interview history of a candidate.
     *
     * @param candidate the candidate entity
     * @return list of all interviews for this candidate
     */
    List<Interview> findByCandidate(Candidate candidate);

    /**
     * Finds a specific interview by candidate and stage.
     * Used to check if an interview already exists for a stage
     * before scheduling a new one.
     *
     * @param candidate      the candidate entity
     * @param interviewStage the stage to check
     * @return Optional containing the interview if found
     */
    Optional<Interview> findByCandidateAndInterviewStage(
            Candidate candidate, InterviewStage interviewStage);
}