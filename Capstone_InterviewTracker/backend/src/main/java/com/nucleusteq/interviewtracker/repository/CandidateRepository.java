package com.nucleusteq.interviewtracker.repository;

import com.nucleusteq.interviewtracker.entity.Candidate;
import com.nucleusteq.interviewtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Candidate entity.
 * Provides database operations for candidate profiles.
 */
@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    /**
     * Finds a candidate by their linked user account.
     * Used to load a candidate's profile after they log in.
     *
     * @param user the user account linked to the candidate
     * @return Optional containing the candidate if found
     */
    Optional<Candidate> findByUser(User user);

    /**
     * Checks if a candidate with the given email already exists.
     * Used to prevent duplicate candidate profiles.
     *
     * @param email the email to check
     * @return true if a candidate with this email exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a candidate with the given mobile number already exists.
     * Used to prevent duplicate candidate profiles.
     *
     * @param mobileNumber the mobile number to check
     * @return true if a candidate with this number exists
     */
    boolean existsByMobileNumber(String mobileNumber);
}