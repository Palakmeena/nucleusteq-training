package com.nucleusteq.interviewtracker.repository;

import com.nucleusteq.interviewtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the given email already exists.
     * Used during registration to prevent duplicate accounts.
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by their secure activation token.
     * Used during the panel member onboarding flow.
     */
    Optional<User> findByActivationToken(String activationToken);
}