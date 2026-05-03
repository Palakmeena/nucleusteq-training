package com.nucleusteq.interviewtracker.repository;

import com.nucleusteq.interviewtracker.entity.Candidate;
import com.nucleusteq.interviewtracker.entity.Interview;
import com.nucleusteq.interviewtracker.enums.InterviewStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
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

    /**
     * Finds a non-completed interview by candidate and stage.
     * Used to check if an active (non-completed) interview already exists
     * before scheduling a new one — allows re-scheduling after rejection.
     */
    Optional<Interview> findByCandidateAndInterviewStageAndIsCompletedFalse(
            Candidate candidate, InterviewStage interviewStage);

        /**
         * Checks whether any interview already occupies the given date and time.
         * Used to prevent double-booking a slot across candidates.
         *
         * @param interviewDate the interview date
         * @param interviewTime the interview time
         * @return true if a conflicting interview already exists
         */
        boolean existsByInterviewDateAndInterviewTime(LocalDate interviewDate, LocalTime interviewTime);

        /**
         * Checks whether any other interview already occupies the given date and time.
         * Used when rescheduling an existing interview so the current row is excluded
         * from the conflict check.
         *
         * @param interviewDate the interview date
         * @param interviewTime the interview time
         * @param id the interview ID to exclude from the duplicate-slot lookup
         * @return true if another interview already uses the slot
         */
        boolean existsByInterviewDateAndInterviewTimeAndIdNot(
                        LocalDate interviewDate, LocalTime interviewTime, Long id);
}