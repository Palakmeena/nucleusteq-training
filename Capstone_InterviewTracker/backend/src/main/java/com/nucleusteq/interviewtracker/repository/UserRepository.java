package com.nucleusteq.interviewtracker.repository;

import com.nucleusteq.interviewtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Spring Data JPA automatically provides basic CRUD operations —
 * we only need to define custom queries here.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * Used during login to load the user and verify credentials.
     * Returns Optional so the caller can handle "user not found" cleanly
     * without getting a NullPointerException.
     *
     * @param email the email address to search for
     * @return an Optional containing the user if found, or empty if not
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the given email already exists.
     * Used during registration to prevent duplicate accounts.
     *
     * @param email the email address to check
     * @return true if a user with this email exists, false otherwise
     */
    boolean existsByEmail(String email);
}